#Simple AWS ANT Task

***Actually only CloudFront invalidation request is provided.***

```
<?xml version="1.0" encoding="utf-8"?>
<project name="MyProjectName" basedir="." default="cdn-invalidation">
	
	<!-- Other informations -->
	
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