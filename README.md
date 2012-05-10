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
<taskdef name="s3put" classpath="aws-ant-task.jar" classname="it.corley.ant.S3PutTask" />
<s3put key="your-key" secret="your-secret" bucket="your-bucket-name" dest="path/to/file">
  <fileset dir="${public.src}" casesensitive="yes">
    <patternset id="non.test.sources">
      <include name="**/*.js"/>
      <exclude name="**/*Test*"/>
    </patternset>
  </fileset>
</s3put>
```

***Mark public files*** using ```publicRead```

***Consider that public property (Grant Everyone open/doownload) is marked on each file scanned
by fileset directive. Your bucket rule is never touched.***

```xml
<s3put
    key="your-key"
    secret="your-secret"
    bucket="your-bucket-name"
    dest="path/to/file"
    publicRead="true">
    <!-- fileset structure -->
</s3put>
```

***Easily configure content types*** using ```contentType property or ContentTypeMapping type```

Global Content-Type configuration:

```xml
<s3put
    key="your-key"
    secret="your-secret"
    bucket="your-bucket-name"
    dest="path/to/file"
    contentType="application/x-whatever">
    <!-- fileset structure -->
</s3put>
```

Content-Type mappers:
<typedef name="contenttype.mapping" classname="it.corley.ant.ContentTypeMapping" classpathref="tasks.path"/>
<s3put
    key="your-key"
    secret="your-secret"
    bucket="your-bucket-name"
    dest="path/to/file"
    contentType="application/x-whatever">
    <fileset dir="dist" include="**/*"/>
    <contenttype.mapping extension=".crx" contenttype="application/x-chrome-extension"/>
    <contenttype.mapping extension=".xpi" contenttype="application/x-xpinstall"/>
</s3put>

Note then when setting global content-type using contentType property of the s3put task
and setting mapping using contenttype.mapping, the mapping takes precedence if given.



### SimpleDB Task

You can insert new rows into your SimpleDB domain using

```xml
<taskdef name="simpledb" classpath="aws-ant-task-${version}.jar" classname="it.corley.ant.SimpleDB" />
<simpledb key="your-key" secret="your-secret" domain="your-domain" region="your-sdb-region">
    <attribute name="my_property" value="my first value" />
    <attribute name="another_property" value="value for this property" />
</simpledb>
```

You have to download the latest ```aws-ant-task.jar``` binary file and add it
into your project. Configure a new task as previous example.

## Building and installing

With tests:

```
cp test.properties.dist test.properties
# Fill the test.properties accordingly
mvn package
```

or without them:

```
mvn package -Dmaven.test.skip=true
```

After properly building the tasks, all the jars needed to use them can be found in target/aws-ant-tasks-0.1-SNAPSHOT-bin
directory. Just copy it whenever you like and use external classpath when defining the tasks:

```xml
<!-- Task for setting up the aws-ant-tasks -->
<target name="awstasks.setup">
    <path id="tasks.path">
        <fileset dir="target/aws-ant-tasks-0.1-SNAPSHOT-bin" includes="*.jar"/>
    </path>

    <taskdef name="s3put" classpath="aws-ant-task-${version}.jar" classname="it.corley.ant.S3PutTask"
             classpathref="tasks.path"/>
    <typedef name="contenttype.mapping" classname="it.corley.ant.ContentTypeMapping" classpathref="tasks.path"/>
</target>


<!--Actual use -->
<target name="use" description="Use the Task" depends="awstasks.setup">

</target>
```

Or copy them to the $ANT_HOME/libs directory and use directly, without specifying the classpath.
