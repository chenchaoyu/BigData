package FileSync;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.omg.CORBA.INITIALIZE;

import Utils.Utils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AbortMultipartUploadRequest;
import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class FileSyncer {
	private final static String serviceEndpoint = "http://scuts3.depts.bingosoft.net:29999";
	private final static String signingRegion = "";
	private static long partSize = 5 << 20;
	private static FileSyncer fileSyncer=new FileSyncer();
	private static String folderPath;
	private static String bucketName;
	private static String accessKey ;
	private static String secretKey ;
	
	private static BasicAWSCredentials credentials;
	private static ClientConfiguration ccfg;
	private static EndpointConfiguration endpoint;
	private static AmazonS3 s3;


	
	private FileSyncer() {
		
	}
	
	public static FileSyncer getInstance() {
		return fileSyncer;	
	}
	
	public static void initialize(String folderPath,String accessKey,String secretKey,String bucketName) {
	
		
		Boolean folderNotNull=!(folderPath.equals(""));
		Boolean accessNotNull=!(accessKey.equals(""));
		Boolean secretNotNull=!(secretKey.equals(""));
		Boolean bucketNotNull=!(bucketName.equals(""));
		
		if(folderNotNull&&accessNotNull&&secretNotNull&&bucketNotNull) {
			FileSyncer.folderPath=folderPath;
			FileSyncer.accessKey=accessKey;
			FileSyncer.secretKey=secretKey;
			FileSyncer.bucketName=bucketName;
		}else {
			FileSyncer.folderPath="E:/test";
			FileSyncer.accessKey="B295DE50D353418FD1F6";
			FileSyncer.secretKey="Wzg1NkVEQjRGMEIwQTRGRTIxM0NDQzgxQjAwQjFGNDg4M0I0NjU5MkVd";
			FileSyncer.bucketName="chenchaoyu";

		}
		
		credentials = new BasicAWSCredentials(FileSyncer.accessKey, FileSyncer.secretKey);
		ccfg = new ClientConfiguration().withUseExpectContinue(false);
		endpoint = new EndpointConfiguration(serviceEndpoint, signingRegion);
		s3= AmazonS3ClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials)).withClientConfiguration(ccfg)
				.withEndpointConfiguration(endpoint).withPathStyleAccessEnabled(true).build();

	}
	
	public void continueUpLoadFile(File file,String uploadId,int n,ArrayList<PartETag> partETags) {

		String filePath = file.getAbsolutePath();
		File folder=new File(folderPath);
		String folderPathConverted=folder.getAbsolutePath();
		int folderStringLen=folderPathConverted.length();
		String keyName=filePath.substring(folderStringLen+1);
		keyName=keyName.replaceAll("\\\\", "/");
		
		// Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
		
		
		long contentLength = file.length();
		if(contentLength>0) {
			
			try {
				
				
				
				System.out.format("Continue upload from last time, upload ID is %s\n", uploadId);

				// Step 1: Upload parts.
				long filePosition = 0+n*partSize;
				
				for (int i=n+1; filePosition < contentLength; i++) {
					// Last part can be less than 5 MB. Adjust part size.
					long pSize = Math.min(partSize, contentLength - filePosition);

					// Create request to upload a part.
					UploadPartRequest uploadRequest = new UploadPartRequest()
							.withBucketName(bucketName)
							.withKey(keyName)
							.withUploadId(uploadId)
							.withPartNumber(i)
							.withFileOffset(filePosition)
							.withFile(file)
							.withPartSize(pSize);

					// Upload part and add response to our list.
					System.out.format("Uploading part %d\n", i);
					partETags.add(s3.uploadPart(uploadRequest).getPartETag());
					filePosition += pSize;
					
					Utils.writetxtfile("0", folderPath+"\\log.txt");
					Utils.filechaseWrite("\n"+filePath+"$$$$"+uploadId+"$$$$"+i+"$$$$", folderPath+"\\log.txt");
					for(PartETag part:partETags) {
						String eTagString=part.getETag();
						int partNumber=part.getPartNumber();
						String partNumberString=Integer.toString(partNumber);
						Utils.filechaseWrite(partNumberString+"$$$$"+eTagString+"$$$$", folderPath+"\\log.txt");
					}
					ArrayList<String> filePaths=FilePathList.getList();
					for(String p:filePaths) {
						Utils.filechaseWrite("\n"+p, folderPath+"\\log.txt");
					}
					
				}

				// Step 3: Complete.
				System.out.println("Completing upload");
				CompleteMultipartUploadRequest compRequest = 
						new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

				s3.completeMultipartUpload(compRequest);
				
				Utils.writetxtfile("0", folderPath+"\\log.txt");
				ArrayList<String> filePaths=FilePathList.getList();
				for(String p:filePaths) {
					Utils.filechaseWrite("\n"+p, folderPath+"\\log.txt");
				}
				
				
			} catch (Exception e) {
				System.err.println(e.toString());
				if (uploadId != null && !uploadId.isEmpty()) {
					// Cancel when error occurred
					System.out.println("Aborting upload");
					s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
				}
				System.exit(1);
			}
		}
		
	}
	public void upLoadFile(File file) {
		
		
		String filePath = file.getAbsolutePath();
		File folder=new File(folderPath);
		String folderPathConverted=folder.getAbsolutePath();
		int folderStringLen=folderPathConverted.length();
		String keyName=filePath.substring(folderStringLen+1);
		keyName=keyName.replaceAll("\\\\", "/");
		
		// Create a list of UploadPartResponse objects. You get one of these
        // for each part upload.
		ArrayList<PartETag> partETags = new ArrayList<PartETag>();
		
		long contentLength = file.length();
		
		String uploadId = null;
		
		if(contentLength>0) {
			
			try {
				// Step 1: Initialize.
				InitiateMultipartUploadRequest initRequest = 
						new InitiateMultipartUploadRequest(bucketName, keyName);
				uploadId = s3.initiateMultipartUpload(initRequest).getUploadId();
				System.out.format("Created upload ID was %s\n", uploadId);

				// Step 2: Upload parts.
				long filePosition = 0;
				
				for (int i=1; filePosition < contentLength; i++) {
					// Last part can be less than 5 MB. Adjust part size.
					long pSize = Math.min(partSize, contentLength - filePosition);

					// Create request to upload a part.
					UploadPartRequest uploadRequest = new UploadPartRequest()
							.withBucketName(bucketName)
							.withKey(keyName)
							.withUploadId(uploadId)
							.withPartNumber(i)
							.withFileOffset(filePosition)
							.withFile(file)
							.withPartSize(pSize);

					// Upload part and add response to our list.
					System.out.format("Uploading part %d\n", i);
					partETags.add(s3.uploadPart(uploadRequest).getPartETag());
					filePosition += pSize;
					
					Utils.writetxtfile("0", folderPath+"\\log.txt");
					boolean success=Utils.filechaseWrite("\n"+filePath+"$$$$"+uploadId+"$$$$"+i+"$$$$", folderPath+"\\log.txt");
					for(PartETag part:partETags) {
						String eTagString=part.getETag();
						int partNumber=part.getPartNumber();
						String partNumberString=Integer.toString(partNumber);
						Utils.filechaseWrite(partNumberString+"$$$$"+eTagString+"$$$$", folderPath+"\\log.txt");
					}
					ArrayList<String> filePaths=FilePathList.getList();
					if(filePaths.size()>0) {
						for(String p:filePaths) {
							Utils.filechaseWrite("\n"+p, folderPath+"\\log.txt");
						}
						
					}
					
				}

				// Step 3: Complete.
				System.out.println("Completing upload");
				CompleteMultipartUploadRequest compRequest = 
						new CompleteMultipartUploadRequest(bucketName, keyName, uploadId, partETags);

				s3.completeMultipartUpload(compRequest);
				
				Utils.writetxtfile("0", folderPath+"\\log.txt");
				ArrayList<String> filePaths=FilePathList.getList();
				for(String p:filePaths) {
					Utils.filechaseWrite("\n"+p, folderPath+"\\log.txt");
				}
			} catch (Exception e) {
				System.err.println(e.toString());
				if (uploadId != null && !uploadId.isEmpty()) {
					// Cancel when error occurred
					System.out.println("Aborting upload");
					s3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, keyName, uploadId));
				}
				System.exit(1);
			}
		}
		
	}
	
	public void downloadFile(File file) {
		//TODO
	}
	
	public void deleteFile(File file) {
		String filePath = file.getAbsolutePath();
		File folder=new File(folderPath);
		String folderPathConverted=folder.getAbsolutePath();
		int folderStringLen=folderPathConverted.length();
		String keyName=filePath.substring(folderStringLen+1);
		keyName=keyName.replaceAll("\\\\", "/");
		
		
			try {
				
				s3.deleteObject(bucketName, keyName);
				
			} catch (Exception e) {
				
			}
			
	}
	
	public void clearBucket() {
		ObjectListing listing=s3.listObjects(bucketName);
		List<S3ObjectSummary> list=listing.getObjectSummaries();
		for(S3ObjectSummary summary :list) {
			String key=summary.getKey();
			s3.deleteObject(bucketName, key);
		}
	}
	
	
	
		
	
}
