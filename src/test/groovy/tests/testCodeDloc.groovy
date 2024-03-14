package tests

import com.squareup.javapoet.CodeBlock

import static TestUtils.*

String str = "serviceCall\\\\\$serviceCall"
logger.info(str)
return CodeBlock.builder().add(str.replace('$', '&'))