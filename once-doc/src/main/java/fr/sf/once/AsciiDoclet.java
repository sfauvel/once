package fr.sf.once;

import java.util.Arrays;

import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;

public class AsciiDoclet {

    private boolean containsTag(MethodDoc methodDoc, String tag) {
        // Could not find qualifedname when the class is not in the path.
        return containsTag(tag, methodDoc.annotations());
    }

    protected boolean containsTag(ClassDoc classDoc, String tag) {
        // Could not find qualifedname when the class is not in the path.
        return containsTag(tag, classDoc.annotations());
    }

    private boolean containsTag(String tag, AnnotationDesc[] annotations) {
        return Arrays.stream(annotations)
                .peek(annotation -> System.out.println("annotation:" + annotation.annotationType().name()))
                .anyMatch(annotation -> tag.equals(annotation.annotationType().name()));
    }

}
