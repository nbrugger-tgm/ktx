### Codegen
Generates KTX DSL Kotlin source files, can be used to for example create a 
SVG KTX or for a native platform

### Generate DSL
```kotlin
val generator = Generator("my.own.ktxdsl.package", filer) {
    yourschema
}
```
**filer**: the generator does not have a default way of writing files, since that depends on the environment where to generate them to and so on. (KSP for example needs a special writer). Its a simple interface just implement it for your needs

**yourschema**: This is the XML schema you want to have the KTX DSL for. You can just instantiate the model with normal constructor calls if you schema is small enought

### Schema from XSD
Do you have a XSD for your XML schema? Then you can just pass the generator the XSD and it will read and build the "yourschema" itself
```kotlin
val generator = Generator("my.own.ktxdsl.package", filer) {
    parseXmlSchema(URI("path/to/your_schema.xsd"))
}
```
> Due to the nature of kotlin KTX cannot enforce `sequence`, `minOccurence` or `maxOccurence`