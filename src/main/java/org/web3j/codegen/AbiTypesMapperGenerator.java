package org.web3j.codegen;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.Fixed;
import org.web3j.abi.datatypes.Int;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Ufixed;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.Utf8String;

/**
 * Generator class for creating all the different numeric type variants.
 */
public class AbiTypesMapperGenerator {

    private static final String CODEGEN_WARNING = "<p>Auto generated code.<br>\n"
            + "<strong>Do not modifiy!</strong><br>\n"
            + "Please use {@link " + AbiTypesMapperGenerator.class.getName() + "} to update.</p>\n";

    private static final String TYPE = "type";

    public static void main(String[] args) throws Exception {
        AbiTypesMapperGenerator abiTypesMapperGenerator = new AbiTypesMapperGenerator();
        if (args.length == 1) {
            abiTypesMapperGenerator.generate(args[0]);
        } else {
            abiTypesMapperGenerator.generate(System.getProperty("user.dir") + "/src/main/java/");
        }
    }

    private void generate(String destinationDir) throws IOException {

        String typesPackageName = "org.web3j.abi.datatypes";
        String autoGeneratedTypesPackageName = typesPackageName + ".generated";

        MethodSpec.Builder builder = MethodSpec.methodBuilder("getType")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(String.class, TYPE)
                .returns(
                        ParameterizedTypeName.get(ClassName.get(Class.class),
                                WildcardTypeName.subtypeOf(Object.class))
                )
                .beginControlFlow("switch (type)");

        builder = addTypes(builder, typesPackageName);
        builder = addGeneratedTypes(builder, autoGeneratedTypesPackageName);
        builder = builder.addStatement("default:\nthrow new $T($S)",
                UnsupportedOperationException.class,
                "Unsupported type encountered");
        builder.endControlFlow();

        MethodSpec methodSpec = builder.build();

        MethodSpec constructorSpec = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .build();

        TypeSpec typeSpec = TypeSpec
                .classBuilder("AbiTypes")
                .addJavadoc(CODEGEN_WARNING)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructorSpec)
                .addMethod(methodSpec)
                .build();

        File path = new File(destinationDir);

        write(autoGeneratedTypesPackageName, typeSpec, path);
    }

    private void write(String packageName, TypeSpec typeSpec, File destination) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .indent("    ")
                .skipJavaLangImports(true)
                .build();

        javaFile.writeTo(destination);
    }

    private MethodSpec.Builder addTypes(MethodSpec.Builder builder, String packageName) {
        builder = addStatement(builder, packageName,
                Address.TYPE_NAME, Address.class.getSimpleName());

        builder = addStatement(builder, packageName,
                Bool.TYPE_NAME, Bool.class.getSimpleName());

        builder = addStatement(builder, packageName,
                Utf8String.TYPE_NAME, Utf8String.class.getSimpleName());

        builder = addStatement(builder, packageName,
                DynamicBytes.TYPE_NAME, DynamicBytes.class.getSimpleName());

        // TODO: Fixed array & dynamic array support
        return builder;
    }

    private MethodSpec.Builder addGeneratedTypes(MethodSpec.Builder builder, String packageName) {

        builder = generateIntTypes(builder, packageName);
        builder = generateFixedTypes(builder, packageName);
        builder = generateFixedBytesTypes(builder, packageName);

        return builder;
    }

    private MethodSpec.Builder generateIntTypes(MethodSpec.Builder builder, String packageName) {
        for (int bitSize = 8; bitSize <= Type.MAX_BIT_LENGTH; bitSize += 8) {

            builder = addStatement(builder, packageName,
                    Uint.TYPE_NAME + bitSize, Uint.class.getSimpleName() + bitSize);
            builder = addStatement(builder, packageName,
                    Int.TYPE_NAME + bitSize, Int.class.getSimpleName() + bitSize);
        }
        return builder;
    }

    private MethodSpec.Builder generateFixedTypes(MethodSpec.Builder builder, String packageName) {
        for (int mBitSize = 8, nBitSize = Type.MAX_BIT_LENGTH - 8;
                mBitSize < Type.MAX_BIT_LENGTH && nBitSize > 0;
                mBitSize += 8, nBitSize -= 8) {
            String suffix = mBitSize + "x" + nBitSize;
            builder = addStatement(
                    builder, packageName, Ufixed.TYPE_NAME + suffix,
                    Ufixed.class.getSimpleName() + suffix);
            builder = addStatement(
                    builder, packageName, Fixed.TYPE_NAME + suffix,
                    Fixed.class.getSimpleName() + suffix);
        }
        return builder;
    }

    private MethodSpec.Builder generateFixedBytesTypes(MethodSpec.Builder builder,
                                                       String packageName) {
        for (int byteSize = 1; byteSize <= 32; byteSize++) {
            builder = addStatement(builder, packageName,
                    Bytes.TYPE_NAME + byteSize, Bytes.class.getSimpleName() + byteSize);
        }
        return builder;
    }

    private MethodSpec.Builder addStatement(MethodSpec.Builder builder, String packageName,
                                            String typeName, String className) {
        return builder.addStatement(
                "case \"$L\":\nreturn $T.class", typeName, ClassName.get(packageName, className));
    }

}
