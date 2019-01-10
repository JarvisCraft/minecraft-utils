# minecraft-utils
A big utility pack for various Minecraft things

## Adding as dependency
### Snapshot builds
Snapshots are available at [Sonatype's OSS repository](https://oss.sonatype.org/content/repositories/snapshots)

For Maven add this to your `pom.xml`
```XML
<repository>
    <id>sonatype-snapshots-repo</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots</url>
</repository>
```
in `<repositories>` section

The version of snapshot builds ends with `-SNAPSHOT`
###### Note that snapshot builds mean that there may be (and regulary are) updates to them without changing their version
