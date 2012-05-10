#Simple AWS ANT Task

Features:

 * CloudFront invalidation requests
 * S3 File upload using fileset strategy
 * SimpleDB insert rows [dynamic attributes]

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

Added a very simple S3 task that enable files upload

```xml
<taskdef name="s3" classpath="aws-ant-task.jar" classname="it.corley.ant.S3PutTask" />
<s3 key="your-key" secret="your-secret" bucket="your-bucket-name" dest="path/to/file">
    <fileset dir="my/dir" includes="**/*.html" />
</s3>
```

File upload using default [Ant fileset](http://ant.apache.org/manual/Types/fileset.html) strategy.

```xml
<taskdef name="s3" classpath="aws-ant-task.jar" classname="it.corley.ant.S3PutTask" />
<s3 key="your-key" secret="your-secret" bucket="your-bucket-name" dest="path/to/file">
  <fileset dir="${public.src}" casesensitive="yes">
    <patternset id="non.test.sources">
      <include name="**/*.js"/>
      <exclude name="**/*Test*"/>
    </patternset>
  </fileset>
</s3>
```

***Mark public files*** using ```publicRead```

***Consider that public property (Grant Everyone open/doownload) is marked on each file scanned 
by fileset directive. Your bucket rule is never touched.***

```xml
<s3 
    key="your-key" 
    secret="your-secret" 
    bucket="your-bucket-name" 
    dest="path/to/file" 
    publicRead="true">
    <!-- fileset structure -->
</s3>
```

### SimpleDB Task

You can insert new rows into your SimpleDB domain using

```xml
<taskdef name="simpledb" classpath="aws-ant-task-${version}.jar" classname="it.corley.ant.SimpleDB" />
<simpledb key="your-key" secret="your-secret" domain="your-domain" region="your-sdb-region">
    <attribute name="my_property" value="my first value" />
    <attribute name="another_property" value="value for this property" />
</simpledb>
```

## Install task

You have to download the latest ```aws-ant-task.jar``` binary file and add it
into your project. Configure a new task as previous example.

## Compile it

If you want to compile by yourself you can use the ```build.xml.dist```. Move it
as ```build.xml``` and run the ```jar``` task.