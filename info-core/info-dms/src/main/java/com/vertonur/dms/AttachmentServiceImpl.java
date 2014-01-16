package com.vertonur.dms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.AttachmentInfoDAO;
import com.vertonur.pojo.AbstractInfo;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.AttachmentInfo.AttachmentType;
import com.vertonur.pojo.User;
import com.vertonur.pojo.config.AttachmentConfig;
import com.vertonur.util.UploadedImageHandler;

public class AttachmentServiceImpl extends GenericService implements
		AttachmentService {

	private AttachmentDAO attachmentDAO;
	private AttachmentInfoDAO attmInfoDao;
	private String thumbPrefix;
	private String uploadRootPath;

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

	@Override
	public void confirmAttachmentUpload(Attachment attm) {
		attachmentDAO.confirmAttachmentUpload(attm);
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

	public Attachment uploadAttchment(InputStream inputStream, int fileSize,
			String mimeType, String fileName, String attmComment,
			ServletContext context, User user, AbstractInfo info)
			throws Exception {

		AttachmentConfig attachmentConfig = getSysAttmConfig();
		int maxAttmSize = attachmentConfig.getMaxSize();
		if (fileSize > 0 && fileSize <= maxAttmSize) {
			String mainTpye = mimeType.split("/")[0];

			Calendar uploadDate = Calendar.getInstance();
			String realFileName = fileName + "_";

			String tmp = mainTpye + "/" + uploadDate.get(Calendar.YEAR)
					+ +uploadDate.get(Calendar.MONTH)
					+ +uploadDate.get(Calendar.DAY_OF_MONTH);
			String phisicalPath = uploadRootPath + "/" + tmp;
			File diskFile = new File(phisicalPath);
			diskFile.mkdirs();

			phisicalPath = phisicalPath + File.separator + realFileName;
			File file = new File(phisicalPath);
			while (file.exists()) {
				int second = Calendar.SECOND;
				int milisecond = Calendar.MILLISECOND;
				phisicalPath = phisicalPath + second + milisecond;
				realFileName = realFileName + second + milisecond;
				file = new File(phisicalPath);
			}
			OutputStream outputStream = new FileOutputStream(phisicalPath);
			byte[] buffer = new byte[32768];
			int n = 0;
			Date startTime = new Date();
			while ((n = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, n);
			}

			Date endTime = new Date();
			long elapsedTime = endTime.getTime() - startTime.getTime();
			inputStream.close();
			outputStream.flush();
			outputStream.close();

			AttachmentInfo attmInfo = new AttachmentInfo();
			attmInfo.setAttachmentType(AttachmentType.LOCAL);
			attmInfo.setMimeType(mimeType);
			attmInfo.setFilesize(fileSize);
			attmInfo.setRealFilename(realFileName);
			attmInfo.setPhysicalFilename(tmp + "/" + realFileName);
			attmInfo.setUploadTime(uploadDate.getTime());
			attmInfo.setComment(attmComment);
			attmInfo.setUploadTimeInMillis(elapsedTime);
			if (mainTpye.equals("image")) {
				attmInfo.setHasThumb(true);
				String extension = fileName.split("\\.")[1];
				UploadedImageHandler.upload(extension, inputStream,
						phisicalPath + thumbPrefix,
						attachmentConfig.getThumbWidth(),
						attachmentConfig.getThumbHeight());
			}

			Attachment attm = new Attachment();
			attm.setUploader(user);
			attm.setAttmHolder(info);
			attmInfo.setAttm(attm);
			saveAttachmentInfo(attmInfo);

			attm.setAttmInfo(attmInfo);
			return attm;
		}

		return null;
	}

	@Override
	public void setThumbPrefix() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUploadRootPath() {
		// TODO Auto-generated method stub

	}
}
