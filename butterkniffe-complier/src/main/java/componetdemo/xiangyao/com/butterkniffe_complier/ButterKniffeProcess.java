package componetdemo.xiangyao.com.butterkniffe_complier;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import componetdemo.xiangyao.com.butterknife_annotion.BindView;


/**
 * @author xiangyao
 */
@AutoService(Processor.class)
public class ButterKniffeProcess extends AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    //    jdk
//
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
//
//        android.util.Log.i(TAG, "process: ");
        System.out.println("-----------------process-----------------------");
        //VariableElement     c成员遍历
        Set<? extends Element> elementSet = roundEnvironment.getElementsAnnotatedWith(BindView.class);
//      1   区分  key是activity  全类名=包名+类名   值   成员变量集合
        Map<String, List<VariableElement>> cacheMap = new HashMap<>();
//MainActivity.class   标签
        for (Element element : elementSet) {
//             全类名=包名+类名
            VariableElement variableElement = (VariableElement) element;
            String activityName = getActivityName(variableElement);

            List<VariableElement> list = cacheMap.get(activityName);
            if (list == null) {
                list = new ArrayList<>();
                cacheMap.put(activityName, list);
            }
//            分类
            list.add(variableElement);
            System.out.println("--------->" + variableElement.getSimpleName().toString());
        }
//  2  为每一个activity生成  Class
//
        Iterator iterator = cacheMap.keySet().iterator();
        while (iterator.hasNext()) {
            //得到mainActivity名字
            String activityName = (String) iterator.next();//奥迪Q3
            //得到mainActivity  控件成员变量的集合
            List<VariableElement> caheElements = cacheMap.get(activityName);//需要用到的零配件

            String newActivityBinder = activityName + "$ViewBinder";
//            工厂
            Filer filer = processingEnv.getFiler();
            try {
                JavaFileObject javaFileObject = filer.createSourceFile(newActivityBinder);
                //相当于工人
                String packageName = getPackageName(caheElements.get(0));
                Writer writer = javaFileObject.openWriter();
//   caheElements.get(0)  VariableElement            caheElements.get(0).getEnclosingElement().   TypeElement  MainActivity$ViewBinder
                String activitySimpleName = caheElements.get(0).getEnclosingElement().getSimpleName().toString() + "$ViewBinder";

//头
                writeHeader(writer, packageName, activityName, activitySimpleName);

//中间部分
                for (VariableElement variableElement : caheElements) {
                    BindView bindView = variableElement.getAnnotation(BindView.class);
                    int id = bindView.value();
                    String fieldName = variableElement.getSimpleName().toString();
                    TypeMirror typeMirror = variableElement.asType();
//                    TextView
                    writer.write("target." + fieldName + "=(" + typeMirror.toString() + ")target.findViewById(" + id + ");");
                    writer.write("\n");
                }


                //结尾部分
                writer.write("\n");
                writer.write("}");
                writer.write("\n");
                writer.write("}");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        return false;
    }

    private void writeHeader(Writer writer, String packageName, String activityName, String activitySimpleName) {

        try {
            writer.write("package " + packageName + ";");
            writer.write("\n");
            writer.write("import componetdemo.xiangyao.com.butterknife.ViewBinder;");
            writer.write("\n");

            writer.write("public class " + activitySimpleName +
                    " implements  ViewBinder<" + activityName + "> {");

            writer.write("\n");
            writer.write(" public void bind( " + activityName + " target) {");
            writer.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String getActivityName(VariableElement variableElement) {

//全类名=包名+类名
//        包名
        String packageName = getPackageName(variableElement);
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
        return packageName + "." + typeElement.getSimpleName().toString();
    }

    private String getPackageName(VariableElement variableElement) {
        TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();//node.getParenNode()
        String packageName = processingEnv.getElementUtils().getPackageOf(typeElement).getQualifiedName().toString();
        System.out.println("-------packageName--------" + packageName);
        return packageName;
    }
}
