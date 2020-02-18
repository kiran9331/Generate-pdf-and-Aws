package com.aws.s3.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;

@Service
public class S3Service {

	private static final Logger logger = LoggerFactory.getLogger(S3Service.class);

	private AmazonS3 s3client;

	@Value("${amazonProperties.endpointUrl}") // TAKING ENDPOINT URL FROM APPLICATION.YML
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}") // TAKING BUCKETNAME FROM APPLICATION.YML
	private String bucketName;
	@Value("${amazonProperties.accessKey}") // TAKING ACCESSKEY FROM APPLICATION.YML
	private String accessKey;
	@Value("${amazonProperties.secretKey}") // TAKING SECRETKEY FROM APPLICATION.YML

	private String secretKey;

	AmazonS3 s3;

	@PostConstruct
	private void initializeAmazon() {
		AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);
		s3 = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
		this.s3client = new AmazonS3Client(credentials);
	}

//	public String uploadFile(MultipartFile multipartFile) {
//		logger.debug("multipartFile::" + multipartFile);
//
//		String fileUrl = "";
//		try {
//			File file = convertMultiPartToFile(multipartFile);
//			logger.debug("convertMultiPartToFile::" + file);
//
//			String fileName = generateFileName(multipartFile);
//			logger.debug("28::" + fileName);
//
//			fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
//			logger.debug("fileUrl::" + fileUrl);
//
//			uploadFileTos3bucket(fileName, file);
//			file.delete();
//		} catch (Exception e) {
//			logger.debug("Exp:" + e);
//			e.printStackTrace();
//		}
//		return fileUrl;
//	}

	public String uploadFile(MultipartFile multipartFile) {
		logger.debug("multipartFile::" + multipartFile);

		String fileUrl = "";
		JSONArray json = new JSONArray();
		try {
			File file = convertMultiPartToFile(multipartFile);
			logger.debug("convertMultiPartToFile::" + file);

			String fileName = generateFileName(multipartFile);
			logger.debug("28::" + fileName);

			fileUrl = endpointUrl + "/" + bucketName + "/" + fileName;
			logger.debug("fileUrl::" + fileUrl);
			json.put(fileUrl);

			uploadFileTos3bucket(fileName, file);
			// file.delete();
		} catch (Exception e) {
			logger.debug("Exp:" + e);
			e.printStackTrace();
		}
		return json.toString();
	}

	private File convertMultiPartToFile(MultipartFile file) throws IOException {
		File convFile = new File(file.getOriginalFilename());
		FileOutputStream fos = new FileOutputStream(convFile);
		fos.write(file.getBytes());
		fos.close();
		return convFile;
	}

	private String generateFileName(MultipartFile multiPart) {
		return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
	}

	private void uploadFileTos3bucket(String fileName, File file) {
		s3client.putObject(
				new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
	}

	public String deleteFileFromS3Bucket(String fileUrl) {
		String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
		logger.debug("file Url service::" + fileUrl);
		logger.debug("bucketName service::" + fileUrl);
		s3client.deleteObject(new DeleteObjectRequest(bucketName, fileName));
		return "Successfully deleted";
	}

	public void uploadMultipleFiles(List<MultipartFile> files) {
		if (files != null) {
			files.forEach(multipartFile -> {
				File file = null;
				try {
					file = convertMultiPartToFile(multipartFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				String uniqueFileName = System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
				uploadFileTos3bucket(uniqueFileName, file);
			});
		}
	}

	public String downloadFile(String fileName) throws IOException {
		logger.debug(" filename local=> " + fileName);

		ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName(bucketName));

		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
			String objectkey = objectSummary.getKey();

			String a = "https://bucketName.s3.us-east-1.amazonaws.com/" + objectkey;
			String b = "https://bucketName.s3.us-east-1.amazonaws.com/" + fileName.replace("\"", "");
			logger.debug(" aaaaaaaa => " + a);
			logger.debug(" bbbbbbbb => " + b);

			logger.debug(" objectkey => " + objectkey);

			if ((fileName.replace("\"", "")).equalsIgnoreCase(objectkey)) {
				logger.debug("In If:" + objectkey);
				logger.debug("URL::" + s3client.getUrl(fileName, objectkey));

				S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, objectkey));
			}
		}
		return fileName;
	}

	public ResponseEntity<byte[]> downloads3bucketfile(String fileName) throws IOException {
		GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, fileName);

		S3Object s3Object = s3client.getObject(getObjectRequest);

		S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

		byte[] bytes = IOUtils.toByteArray(objectInputStream);

		String fileNamea = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

		logger.debug(" fileNaAMEA ======-=-=-=-=>  " + fileNamea);

		File file = new File(fileNamea);

		logger.debug(" extension =>  " + getFileExtension(file));
		HttpHeaders httpHeaders = new HttpHeaders();
		if (getFileExtension(file).contains("png") || getFileExtension(file).contains("jpeg")
				|| getFileExtension(file).contains("jpg")) {
			httpHeaders.setContentType(MediaType.IMAGE_PNG);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileNamea);
			logger.debug(" this is png  jpeg jpg");
		} else if (getFileExtension(file).contains("pdf")) {
			httpHeaders.setContentType(MediaType.APPLICATION_PDF);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileNamea);
			logger.debug(" this is pdf ");
		} else if (getFileExtension(file).contains("txt")) {
			httpHeaders.setContentType(MediaType.TEXT_PLAIN);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileNamea);
			logger.debug(" this is txt ");
		} else {
			httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			httpHeaders.setContentLength(bytes.length);
			httpHeaders.setContentDispositionFormData("attachment", fileNamea);
			logger.debug(" this is else ");
		}
		return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
	}

	public String downloadLinks3bucket(String fileName) {
		String splitname = fileName.replace("\"", "");
		JSONObject jsonObject = new JSONObject();

		String link = null;
		ObjectListing objectListing = s3client.listObjects(new ListObjectsRequest().withBucketName(bucketName));

		for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {

			String objectkey = objectSummary.getKey();

			logger.debug(" objectkey => " + objectkey);

			if ((fileName.replace("\"", "")).equalsIgnoreCase(objectkey)) {

				logger.debug("In If:" + objectkey);

				S3Object s3object = s3.getObject(new GetObjectRequest(bucketName, objectkey));

				logger.debug(" s3object =>  " + s3object.getKey());

				link = "https://bucketName.s3.us-east-2.amazonaws.com/" + objectkey;

				try {
					jsonObject.put("link", link);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				logger.debug(" jsonObject => " + jsonObject);

				logger.debug(
						" link ========================================================================>  " + link);
			}
		}
		return jsonObject.toString();
	}

	// GET FILE EXTENSION
	private static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	public String creates3bucket(String newbucketName) {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_2).build();
		JSONObject jsonObject = new JSONObject();
		Bucket b = null;
		String bucket_name = newbucketName.replace("\"", "");
		if (s3client.doesBucketExistV2(bucket_name)) {
			System.out.format("Bucket %s already exists.\n", bucket_name);
			// b = getBucket(bucket_name);
			try {
				String textMsg = " Already Exist Bucket Name ";
				jsonObject.put(" response ", textMsg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				b = s3client.createBucket(bucket_name);
				try {
					jsonObject.put(" bucket_name ", bucket_name);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
		}
		return jsonObject.toString();

		/* CREATING FOLDER INSIDE EXISTING BUCKET */
		/*
		 * logger.debug("BName::" + bucketName);
		 * 
		 * AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		 * logger.debug("credentials::" + credentials);
		 * 
		 * AmazonS3 s3 = new AmazonS3Client(credentials, new
		 * ClientConfiguration().withProtocol(Protocol.HTTP));
		 * 
		 * String bucketNameNew = bucketName; // existing bucket name String key =
		 * "taxgenie/"; InputStream input = new ByteArrayInputStream(new byte[0]);
		 * ObjectMetadata metadata = new ObjectMetadata(); metadata.setContentLength(0);
		 * s3.putObject(new PutObjectRequest(bucketNameNew, key, input, metadata));
		 */
	}

	public String creates3bucketfinal(String newbucketName) {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

		AmazonS3 s3client = AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_2).build();
		JSONObject jsonObject = new JSONObject();
		Bucket b = null;
		String bucket_name = newbucketName.replace("\"", "");
		if (s3client.doesBucketExistV2(newbucketName)) {
			System.out.format("Bucket %s already exists.\n", newbucketName);
			b = getBucket(newbucketName);
			try {
				String textMsg = " Already Exist Bucket Name ";
				jsonObject.put(" response ", textMsg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			try {
				b = s3client.createBucket(newbucketName);
				try {
					jsonObject.put(" bucket_name ", bucket_name);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} catch (AmazonS3Exception e) {
				System.err.println(e.getErrorMessage());
			}
		}
		System.out.println(b);
		return jsonObject.toString();
	}

	private com.amazonaws.services.s3.model.Bucket getBucket(String bucketNamel) {
		// TODO Auto-generated method stub
		return null;
	}

//	
//	
//	
//	public String creates3bucket(String NewbucketName) {
//
//		
//	}

	/*
	 * public byte[] getFile(String key) { S3Object obj =
	 * s3client.getObject(bucketName, "C:/Users/Admin/Desktop/S3"+"/"+key);
	 * S3ObjectInputStream stream = obj.getObjectContent(); try { byte[] content =
	 * IOUtils.toByteArray(stream); obj.close(); return content; } catch
	 * (IOException e) { e.printStackTrace(); } return null; }
	 */

}