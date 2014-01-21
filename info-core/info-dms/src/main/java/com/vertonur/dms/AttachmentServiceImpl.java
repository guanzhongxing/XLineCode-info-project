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

	public AttachmentConfig getSysAttmConfig() {
		AttachmentConfig config = new RuntimeParameterServiceImpl()
				.getAttachmentConfig();
		return config;
	}

	public AttachmentConfig getAttmConfigById(int id) {
		return attachmentDAO.getAttmConfigById(id);
	}

	public Attachment getAttmById(int attmId) {
		return attachmentDAO.getAttmById(attmId);
	}

	public void updateAttachment(Attachment attm) {
		attachmentDAO.updateAttachment(attm);
	}

	public Integer saveAttachment(Attachment attm) {
		return attachmentDAO.saveAttachment(attm);
	}

	public Integer saveAttachmentInfo(AttachmentInfo attmInfo) {
		return attmInfoDao.saveAttachmentInfo(attmInfo);
	}

	@Override
	public void deleteAttachment(Attachment attm) {
		attachmentDAO.deleteAttachment(attm);
	}

	// TODO:make setUploadConfirmed private and add reflection code to set
	// confirmed to true
	@Override
	public void confirmEmbeddedImageUpload(Attachment attm) {
		AttachmentInfo info = attm.getAttmInfo();
		info.setUploadConfirmed(true);
		updateAttachment(attm);
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

	public Attachment uploadInfoEmbededImage(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException {

		Attachment attachment = uploadAttchment(attachmentType, inputStream,
				mimeType, uploadRoot, fileName, fileSize, false, attmComment,
				user, info);
		attachment.getAttmInfo().setFileType(FileType.EMBEDDED_IMAGE);
		updateAttachment(attachment);

		return attachment;
	}

	public Attachment uploadImage(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException {

		Attachment attachment = uploadAttchment(attachmentType, inputStream,
				mimeType, uploadRoot, fileName, fileSize, true, attmComment,
				user, info);
		attachment.getAttmInfo().setFileType(FileType.FILE);
		attachment.getAttmInfo().setUploadConfirmed(true);
		updateAttachment(attachment);

		return attachment;
	}

	public Attachment uploadAttchment(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, String attmComment, User user,
			AbstractInfo info) throws AttachmentSizeExceedException,
			IOException {
		Attachment attachment = uploadAttchment(attachmentType, inputStream,
				mimeType, uploadRoot, fileName, fileSize, false, attmComment,
				user, info);
		attachment.getAttmInfo().setFileType(FileType.FILE);
		attachment.getAttmInfo().setUploadConfirmed(true);
		updateAttachment(attachment);

		return attachment;
	}

	private Attachment uploadAttchment(AttachmentType attachmentType,
			InputStream inputStream, String mimeType, String uploadRoot,
			String fileName, long fileSize, boolean generateThumb,
			String attmComment, User user, AbstractInfo info)
			throws AttachmentSizeExceedException, IOException {
		AttachmentConfig attachmentConfig = getSysAttmConfig();
		long maxAttmSize = attachmentConfig.getMaxSize();
		if (fileSize > maxAttmSize)
			throw new AttachmentSizeExceedException(fileSize, maxAttmSize);

		String mainTpye = mimeType.split("/")[0];
		Calendar uploadDate = Calendar.getInstance();
		String realFileName = RandomStringUtils.randomAlphabetic(6) + "_"
				+ fileName;

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String tmp = mainTpye + "/" + format.format(uploadDate.getTime());
		String fullPath = uploadRoot + "/" + tmp;

		AttachmentInfo attmInfo = new AttachmentInfo();
		attmInfo.setAttachmentType(attachmentType);
		attmInfo.setMimeType(mimeType);
		attmInfo.setFilesize(fileSize);
		attmInfo.setRealFilename(realFileName);
		attmInfo.setPhysicalFilename(tmp + "/" + realFileName);
		attmInfo.setUploadTime(uploadDate.getTime());
		attmInfo.setComment(attmComment);

		Date startTime = new Date();
		if (AttachmentType.LOCAL.equals(attachmentType)) {
			inputStream = upload2Local(inputStream, fullPath, realFileName);
			if (mainTpye.equals("image") && generateThumb) {
				attmInfo.setHasThumb(true);
				String extension = fileName.split("\\.")[1];
				UploadedImageHandler.upload(
						extension,
						inputStream,
						fullPath + "/" + realFileName
								+ attachmentConfig.getThumbPrefix(),
						attachmentConfig.getThumbWidth(),
						attachmentConfig.getThumbHeight());
			}

		} else {
			String downloadUrl = upload2Bcs(inputStream, fullPath + "/"
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

	private InputStream upload2Local(InputStream inputStream,
			String phisicalPath, String realFileName) throws IOException {
		File diskFile = new File(phisicalPath);
		diskFile.mkdirs();

		phisicalPath = phisicalPath + File.separator + realFileName;
		OutputStream outputStream = new FileOutputStream(phisicalPath);
		byte[] buffer = new byte[32768];
		int n = 0;

		while ((n = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, n);
		}

		inputStream.close();
		outputStream.flush();
		outputStream.close();

		diskFile = new File(phisicalPath);
		inputStream = new FileInputStream(diskFile);
		return inputStream;
	}

	private String upload2Bcs(InputStream inputStream, String phisicalPath,
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
		PutObjectRequest request = new PutObjectRequest(bucket, phisicalPath,
				inputStream, objectMetadata);
		BaiduBCSResponse<ObjectMetadata> response = baiduBCS.putObject(request);
		response.getResult();

		GenerateUrlRequest generateUrlRequest = new GenerateUrlRequest(
				HttpMethodName.GET, bucket, phisicalPath);
		String downloadableUrl = baiduBCS.generateUrl(generateUrlRequest);

		return downloadableUrl;
	}
}
