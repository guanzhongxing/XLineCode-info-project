package com.vertonur.dms;

import java.util.Calendar;
import java.util.List;

import com.vertonur.dao.api.AttachmentDAO;
import com.vertonur.dao.api.AttachmentInfoDAO;
import com.vertonur.pojo.Attachment;
import com.vertonur.pojo.AttachmentInfo;
import com.vertonur.pojo.config.AttachmentConfig;

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
	
	public Attachment uploadAttchment(FormFile uploadedFile, String uploadRoot,
			String attmComment, ServletContext context, User theCurrentUser,
			Info info) throws Exception {

		AttachmentConfig attachmentConfig = getSysAttmConfig();
		int maxAttmSize = attachmentConfig.getMaxSize();
		if (uploadedFile != null && uploadedFile.getFileSize() > 0
				&& uploadedFile.getFileSize() <= maxAttmSize) {
			String mimeType = uploadedFile.getContentType();
			String mainTpye = mimeType.split("/")[0];

			Calendar uploadDate = Calendar.getInstance();
			String realFileName = uploadedFile.getFileName() + "_";

			String tmp = mainTpye + "/" + uploadDate.get(Calendar.YEAR) + "/"
					+ uploadDate.get(Calendar.MONTH) + "/"
					+ uploadDate.get(Calendar.DAY_OF_MONTH);
			String phisicalPath = context.getRealPath(SystemConfig.getConfig()
					.getRuntimeConfig().getUploadRootFolder()
					+ "/" + tmp);
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
			InputStream inputStream = uploadedFile.getInputStream();
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
			attmInfo.setFilesize(uploadedFile.getFileSize());
			attmInfo.setRealFilename(realFileName);
			attmInfo.setPhysicalFilename(tmp + "/" + realFileName);
			attmInfo.setUploadTime(uploadDate.getTime());
			attmInfo.setComment(attmComment);
			attmInfo.setUploadTimeInMillis(elapsedTime);
			if (mainTpye.equals("image")) {
				attmInfo.setHasThumb(true);
				String extension = uploadedFile.getFileName().split("\\.")[1];
				UploadedImageHandler.upload(extension,
						uploadedFile.getInputStream(), phisicalPath
								+ SystemConfig.getConfig().getRuntimeConfig()
										.getThumbPrefix(),
						attachmentConfig.getThumbWidth(),
						attachmentConfig.getThumbHeight());
			}
			uploadedFile.destroy();

			Attachment attm = new Attachment();
			attm.setUploader(theCurrentUser.getCore());
			attm.setAttmHolder(info.getCore());
			attmInfo.setAttm(attm);
			AttachmentService attachmentService = SystemContextService
					.getService().getDataManagementService(
							ServiceEnum.ATTACHMENT_SERVICE);
			attachmentService.saveAttachmentInfo(attmInfo);

			attm.setAttmInfo(attmInfo);
			return attm;
		}

		return null;
	}
}
