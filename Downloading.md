  1. download the latest ZIP file from http://code.google.com/p/litemvc/downloads/list

or

  1. follow [Checkout](http://code.google.com/p/litemvc/source/checkout) instructions to get the source
  1. run ant dist to build the latest jar file

or

Use maven:
```
	<repositories>
		<!-- ... -->
		<repository>
			<id>litemvc-repository</id>
			<url>http://litemvc.googlecode.com/svn/maven</url>
		</repository>
		<!-- ... -->
	</repositories>


	<dependencies>
		<!-- ... -->
		<dependency>
			<groupId>com.litemvc</groupId>
			<artifactId>litemvc</artifactId>
			<version><!-- whatever is the latest version --></version>
		</dependency>
		<!-- ... -->
	</dependencies>
```