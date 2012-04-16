#Simple AWS ANT Task

***Actually only CloudFront invalidation request is provided.***

```xml
<?xml version="1.0" encoding="utf-8"?>
<project name="MyProjectName" basedir="." default="cdn-invalidation">
	
	<!-- Other ant tasks -->
	
	<target name="cdn-invalidation" description="Invalidation of static files">
        <taskdef name="cloudfront" classpath="aws-ant-task.jar" classname="it.corley.ant.CloudFront" />
        <cloudfront key="your-key" secret="your-secret-key" distributionId="your-distribution-id">
        	<delete path="/js/folder/my-path.js"/>
        	<delete path="/css/folder/my-path.css"/>
        	<delete path="/direct-gen.txt"/>
        </cloudfront>
    </target>
</project>
```

### Simple S3 Task

Added a very simple S3 task that enable a single upload.

```xml
<taskdef name="s3" classpath="aws-ant-task.jar" classname="it.corley.ant.S3" />
<s3 key="your-key" secret="your-secret" bucket="your-bucket-name" dest="path/to/file bundleUpload="path/file/to"/>
```

## Install task

You have to download the latest ```aws-ant-task.jar``` binary file and add it
into your project. Configure a new task as previous example.

## Compile it

If you want to compile by yourself you can use the ```build.xml.dist```. Move it
as ```build.xml``` and run the ```jar``` task.