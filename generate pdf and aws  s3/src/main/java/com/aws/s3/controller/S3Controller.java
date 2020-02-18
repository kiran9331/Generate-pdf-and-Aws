package com.aws.s3.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.aws.s3.service.S3Service;

@RestController
public class S3Controller {

	private static final Logger logger = LoggerFactory.getLogger(S3Controller.class);

	@Value("${amazonProperties.endpointUrl}") // TAKING ENDPOINT URL FROM APPLICATION.YML
	private String endpointUrl;
	@Value("${amazonProperties.bucketName}") // TAKING BUCKETNAME FROM APPLICATION.YML
	private String bucketName;
	@Value("${amazonProperties.accessKey}") // TAKING ACCESSKEY FROM APPLICATION.YML
	private String accessKey;
	@Value("${amazonProperties.secretKey}") // TAKING SECRETKEY FROM APPLICATION.YML
	private String secretKey;

	@Autowired
	private AmazonS3Client amazonS3Client;

	AmazonS3 s3;

	private AmazonS3 s3client;

	@Autowired
	S3Service s3Service;

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public AmazonS3Client amazonS3Client(AWSCredentials awsCredentials) {
		AmazonS3Client amazonS3Client = new AmazonS3Client(awsCredentials);
		return amazonS3Client;
	}

//	@RequestMapping(value = "/uploadfiletos3bucket", method = RequestMethod.POST, headers = "Accept=application/json")
//	public String uploadFile(@RequestParam String companyID, @RequestParam("file") MultipartFile file)
//			throws IOException {
//		logger.debug("uploadfiletos3bucket::" + file.getOriginalFilename());
//		return this.s3Service.uploadFile(file);
//	}

	@RequestMapping(value = "/uploadfiletos3bucket", method = RequestMethod.POST, headers = "Accept=application/json")
	public String uploadFile(@RequestParam String companyID, @RequestParam("file") MultipartFile file)
			throws IOException {
		logger.debug("uploadfiletos3bucket::" + file.getOriginalFilename());
		String resp = s3Service.uploadFile(file);
		System.out.println("resp" + resp);
		return resp;
	}

	@RequestMapping(value = "/deletefilefroms3bucket", method = RequestMethod.DELETE, headers = "Accept=application/json")
	public String deleteFile(@RequestPart(value = "url") String fileUrl) {
		logger.debug("fileUrl::: " + fileUrl);
		return this.s3Service.deleteFileFromS3Bucket(fileUrl);
	}

	@RequestMapping(value = "/uploadmultiplefiles", method = RequestMethod.POST, headers = "Accept=application/json")
	public void uploadMultipleFiles(@RequestParam("file") List<MultipartFile> file) {
		logger.debug("files multiple::: " + file.get(0) + file.get(1));
		s3Service.uploadMultipleFiles(file);
	}

	/* Using this currently */
	@RequestMapping(value = "/downloadfilefroms3bucket", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<byte[]> downloadFile(@RequestParam String fileName) throws IOException {
		logger.debug("uploadfiletos3bucket::" + fileName);
		// return this.s3Service.downloadFile(fileName);
		return this.s3Service.downloads3bucketfile(fileName);
	}

	@RequestMapping(value = "/downloadLinksinjsonobject3bucket", method = RequestMethod.GET, headers = "Accept=application/json")
	public ResponseEntity<?> downloadLinks3bucket(@RequestParam String fileName) throws IOException {
		String resp;
		// resp = this.s3Service.downloadLinks3bucket(fileName);
		resp = this.s3Service.downloadLinks3bucket(fileName);
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	// Create Bucket New Final
	@RequestMapping(value = "/creates3bucketfinal", method = RequestMethod.POST, headers = "Accept=application/json")
	public ResponseEntity<?> creates3bucketfinal(@RequestParam String NewbucketName) {
		String resp = this.s3Service.creates3bucketfinal(NewbucketName);
		return new ResponseEntity<>(resp, HttpStatus.OK);
	}

	/*
	 * @RequestMapping(value = "/creates3bucket", method = RequestMethod.POST,
	 * headers = "Accept=application/json") public String createBucket(@RequestParam
	 * String bucketNamel) {
	 * 
	 * AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
	 * 
	 * AmazonS3 s3client = AmazonS3ClientBuilder.standard() .withCredentials(new
	 * AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_2).
	 * build(); JSONObject jsonObject = new JSONObject(); Bucket b = null; String
	 * bucket_name = bucketNamel.replace("\"", ""); if
	 * (s3client.doesBucketExistV2(bucket_name)) {
	 * System.out.format("Bucket %s already exists.\n", bucket_name); b =
	 * getBucket(bucket_name); try { String textMsg= " Already Exist Bucket Name ";
	 * jsonObject.put(" response ", textMsg); } catch (JSONException e) {
	 * e.printStackTrace(); } } else { try { b = s3client.createBucket(bucket_name);
	 * try { jsonObject.put(" bucket_name ", bucket_name); } catch (JSONException e)
	 * { e.printStackTrace(); } } catch (AmazonS3Exception e) {
	 * System.err.println(e.getErrorMessage()); } } System.out.println(b); return
	 * jsonObject.toString();
	 */

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
	 * s3.putObject(new PutObjectRequest(bucketNameNew, key, input, metadata));}
	 */

	private com.amazonaws.services.s3.model.Bucket getBucket(String bucketNamel) {
		// TODO Auto-generated method stub
		return null;
	}

}
