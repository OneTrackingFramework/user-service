# user-service
Manage user accounts

## Getting started

In order to pull in the dependencies from Github packages, you need to enable authentication for two Maven repositories. You need to configure the following servers in your `settings.xml`, which by default is located in `~/.m2/settings.xml`. This file should look something like this, either replace the environment variables or set them in your shell.

```xml
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <servers>
    <server>
      <id>com.github.OneTrackingFramework.kafka-module-boot</id>
      <username>${env.GITHUB_ACTOR}</username>
      <password>${env.GITHUB_TOKEN}</password>
    </server>
    <server>
      <id>com.github.OneTrackingFramework.commons-boot</id>
      <username>${env.GITHUB_ACTOR}</username>
      <password>${env.GITHUB_TOKEN}</password>
    </server>
  </servers>

</settings>
```

If you want to store this file in another directory than your home directory, then you need to set this path using the `-s` flag when invoking Maven, e.g. `mvn -s  ./settings.xml`.