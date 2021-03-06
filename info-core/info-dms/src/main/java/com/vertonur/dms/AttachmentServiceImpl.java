package com.vertonur.dms;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;

import com.baidu.inf.iis.bcs.BaiduBCS;
import com.baidu.inf.iis.bcs.auth.BCSCredentials;
import com.baidu.inf.iis.bcs.http.HttpMethodName;
import com.baidu.inf.iis.bcs.model.ObjectMetadata;
import com.baidu.inf.iis.bcs.request.GenerateUrlRequest;
import com.baidu.inf.iis.bcs.request.PutObjectRequest;
import com.baidu.inf.iis.bcs.response.BaiduBCSResponse;
import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.AttachmentInfoDAO;
import com.vertonur.dms.exception.AttachmentSizeExceedException;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.AttachmentInfo.FileType;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.util.UploadedImageHandler;

public class AttachmentServiceImpl extends GenericService implements
		AttachmentService {

	private AttachmentDAO attachmentDAO;
	private AttachmentInfoDAO attmInfoDao;

	protected AttachmentServiceImpl() {
		attachmentDAO = manager.getAttachmentDAO();
		attmInfoDao = manager.getAttachmentInfoDAO();
	}

	@Override
	public AttachmentConfig getSysAttmConfig() {
		AttachmentConfig config = new RuntimeParameterServiceImpl()
				.getAttachmentConfig();
		return config;
	}

	@Override
	public AttachmentConfig getAttmConfigById(int id) {
		return attachmentDAO.getAttmConfigById(id);
	}

	@Override
	public Attachment getAttmById(int attmId) {
		return attachmentDAO.getAttmById(attmId);
	}

	@Override
	public void updateAttachment(Attachment attm) {
		attachmentDAO.updateAttachment(attm);
	}

	protected Integer saveAttachment(Attachment attm) {
		return attachmentDAO.saveAttachment(attm);
	}

	@Override
	public Integer saveAttachmentInfo(AttachmentInfo attmInfo) {
		return attmInfoDao.saveAttachmentInfo(attmInfo);
	}

	@Override
	public void deleteAttachment(Attachment attm) {
		AttachmentInfo info = attm.getAttmInfo();
		if (AttachmentType.BCS.equals(info.getAttachmentType())) {
			deleteBcsAttachment(info);
		} else if (AttachmentType.LOCAL.equals(info.getAttachmentType())) {
			String filePath = info.getFilePath();
			File attachment = new File(filePath);
			if (attachment.exists())
				attachment.delete();

			String mainTpye = info.getMimeType().split("/")[0];
			if (mainTpye.equals("image")
					&& !FileType.EMBEDDED_IMAGE.equals(info.getFileType())) {
				AttachmentConfig attachmentConfig = getSysAttmConfig();
				String thumbPath = filePath + attachmentConfig.getThumbSuffix();
				File thumb = new File(thumbPath);
				if (thumb.exists())
					thumb.delete();
			}
		}
		attachmentDAO.deleteAttachment(attm);
	}

	private void deleteBcsAttachment(AttachmentInfo attachmentInfo) {
		AttachmentConfig attachmentConfig = getSysAttmConfig();
		BCSCredentials credentials = new BCSCredentials(
				attachmentConfig.getBcsAccessKey(),
				attachmentConfig.getBcsSecretKey());
		BaiduBCS baiduBCS = new BaiduBCS(credentials,
				attachmentConfig.getBcsHost());
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8

		String bucket = attachmentConfig.getBcsBucket();
		String filePath = attachmentInfo.getFilePath();
		if (baiduBCS.doesObjectExist(bucket, filePath))
			baiduBCS.deleteObject(bucket, filePath);
	}

	// TODO:make setUploadConfirmed private and add reflection code to set
	// confirmed to true
	@Override
	public void confirmEmbeddedImageUpload(AbstractInfo holder, int attachmentId) {
		Attachment attachment = attachmentDAO.getAttmById(attachmentId);
		AttachmentInfo info = attachment.getAttmInfo();
		info.setUploadConfirmed(true);
		attachment.setAttmHolder(holder);
		updateAttachment(attachment);
	}

	@Override
	public String downloadAttachment(int attmId) {
		Attachment attachment = attachmentDAO.getAttmById(attmId);
		AttachmentInfo attmInfo = attachment.getAttmInfo();
		attmInfo.increaseDownloadCountByOne();
		updateAttachment(attachment);

		return attmInfo.getDownloadUrl();
	}

	@Override
	public List<Attachment> getBcsAttms() {
		return attachmentDAO.getBcsAttms();
	}

	public Attachment uploadInfoEmbededImage(InputStream inputStream,
			String mimeType, String fileName, long fileSize, User user)
			throws AttachmentSizeExceedException, IOException {

		AttachmentConfig attachmentConfig = getSysAttmConfig();
		Attachment attachment = handleAttachmentUpload(attachmentConfig,
				FileType.EMBEDDED_IMAGE, inputStream, mimeType,
				attachmentConfig.getUploadRoot(), fileName, fileSize, null,
				user, null);
		attachment.getAttmInfo().setFileType(FileType.EMBEDDED_IMAGE);
		updateAttachment(attachment);

		return attachment;
	}

	public Attachment uploadAttachment(InputStream inputStream,
			String mimeType, String fileName, long fileSize,
			String attmComment, User user, AbstractInfo info)
			throws AttachmentSizeExceedException, IOException {
		AttachmentConfig attachmentConfig = getSysAttmConfig();
		return uploadAttachment(attachmentConfig, inputStream, mimeType,
				attachmentConfig.getUploadRoot(), fileName, fileSize,
				attmComment, user, info);
	}

	protected Attachment uploadAttachment(AttachmentConfig attachmentConfig,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException {
		Attachment attachment = handleAttachmentUpload(attachmentConfig,
				FileType.FILE, inputStream, mimeType, uploadRoot, fileName,
				fileSize, attmComment, user, info);
		attachment.getAttmInfo().setFileType(FileType.FILE);
		attachment.getAttmInfo().setUploadConfirmed(true);
		updateAttachment(attachment);

		return attachment;
	}

	private Attachment handleAttachmentUpload(
			AttachmentConfig attachmentConfig, FileType fileType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException {
		long maxAttmSize = attachmentConfig.getMaxSize();
		if (fileSize > maxAttmSize)
			throw new AttachmentSizeExceedException(fileSize, maxAttmSize);

		String mainTpye = mimeType.split("/")[0];
		Calendar uploadDate = Calendar.getInstance();
		String realFileName = RandomStringUtils.randomAlphabetic(6) + "_"
				+ fileName;

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String filePath = uploadRoot + "/" + mainTpye + "/"
				+ format.format(uploadDate.getTime());

		AttachmentInfo attmInfo = new AttachmentInfo();
		AttachmentType attachmentType = attachmentConfig.getUploadFileSystem();
		attmInfo.setAttachmentType(attachmentType);
		attmInfo.setMimeType(mimeType);
		attmInfo.setFilesize(fileSize);
		attmInfo.setFileName(realFileName);
		attmInfo.setFilePath(filePath + "/" + realFileName);
		attmInfo.setUploadTime(uploadDate.getTime());
		attmInfo.setComment(attmComment);

		Date startTime = new Date();
		if (AttachmentType.LOCAL.equals(attachmentType)) {
			inputStream = upload2Local(inputStream, filePath, realFileName);
			if (mainTpye.equals("image") && FileType.FILE.equals(fileType)) {
				attmInfo.setHasThumb(true);
				String extension = fileName.split("\\.")[1];
				UploadedImageHandler.upload(
						extension,
						inputStream,
						filePath + "/" + realFileName
								+ attachmentConfig.getThumbSuffix(),
						attachmentConfig.getThumbWidth(),
						attachmentConfig.getThumbHeight());
			}
		} else {
			String downloadUrl = upload2Bcs(inputStream, filePath + "/"
					+ realFileName, mimeType, fileSize);
			attmInfo.setDownloadUrl(downloadUrl);
		}
		inputStream.close();
		Date endTime = new Date();
		long elapsedTime = endTime.getTime() - startTime.getTime();
		attmInfo.setUploadTimeInMillis(elapsedTime);

		Attachment attm = new Attachment();
		attm.setUploader(user);
		attm.setAttmHolder(info);
		attm.setAttmInfo(attmInfo);
		saveAttachment(attm);
		return attm;
	}

	protected InputStream upload2Local(InputStream inputStream,
			String filePath, String realFileName) throws IOException {
		File diskFile = new File(filePath);
		diskFile.mkdirs();

		filePath = filePath + File.separator + realFileName;
		OutputStream outputStream = new FileOutputStream(filePath);
		byte[] buffer = new byte[32768];
		int n = 0;

		while ((n = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, n);
		}

		inputStream.close();
		outputStream.flush();
		outputStream.close();

		diskFile = new File(filePath);
		inputStream = new FileInputStream(diskFile);
		return inputStream;
	}

	protected String upload2Bcs(InputStream inputStream, String filePath,
			String contentType, long fileSize) {
		AttachmentConfig attachmentConfig = getSysAttmConfig();
		BCSCredentials credentials = new BCSCredentials(
				attachmentConfig.getBcsAccessKey(),
				attachmentConfig.getBcsSecretKey());
		BaiduBCS baiduBCS = new BaiduBCS(credentials,
				attachmentConfig.getBcsHost());
		baiduBCS.setDefaultEncoding("UTF-8"); // Default UTF-8
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(contentType);
		objectMetadata.setContentLength(fileSize);

		String bucket = attachmentConfig.getBcsBucket();
		PutObjectRequest request = new PutObjectRequest(bucket, filePath,
				inputStream, objectMetadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		response.getResult();

		GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(
				HttpMethodName.GET, bucket, filePath);
		String downloadableUrl = baiduBCS.generateUrl(generateUrlRequest);

		return downloadableUrl;
	}
}
