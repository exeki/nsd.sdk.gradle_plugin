package tests.files


File checkDirectory(File file) {
    List<File> toCreate = []
    File parentFile = null
    if (file.isDirectory()) parentFile = file
    else parentFile = file.parentFile
    while (!parentFile.exists()) {
        toCreate.add(parentFile)
        parentFile = parentFile.parentFile
    }
    toCreate.reverse().forEach(File::mkdirs)
    return file
}


String from = 'C:\\Users\\ekazantsev\\nsd_sdk\\data\\nsd_fake_classes\\build\\libs\\nsd_fake_classes-1.0.0-javadoc.jar'
String to = 'C:\\Users\\ekazantsev\\nsd_sdk\\out\\out\\out\\out\\out\\out\\someFile.jar'

File fromFile = new File(from)
File toFile = new File(to)

return toFile.mkdirs()
println(toFile.parentFile.exists())
println(toFile.parentFile.name)
toFile.parentFile.mkdirs()
println(toFile.parentFile.parentFile.exists())
println(toFile.parentFile.parentFile.name)
//toFile.newOutputStream().write(fromFile.newInputStream().readAllBytes())