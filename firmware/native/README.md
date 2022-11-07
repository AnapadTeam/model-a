# Native Firmware
This project is for using the Java Native Interface (JNI) to interface low-level C code with Java code for Model A's firmware.

Execute the following command to format the source code (requires `clang-format`):
```shell
./scripts/format_source.sh
```

Execute the following command to generate the C header files for JNI (requires `java`):
```shell
./scripts/generate_jni_headers.sh
```
