package com.myke.doc;

import com.myke.hello.HelloApplication;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * user: zhangjianbin <br/>
 * date: 2018/1/24 10:29
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = HelloApplication.class)
public class Swagger2MarkupTest {

    /**
     * MarkupLanguage.ASCIIDOC：指定了要输出的最终格式。
     * 除了asciidoc之外，还有Markdown和confluence_markup
     * <p>
     * from(newURL("http://localhost:8080/v2/api-docs")：指定了生成静态部署文档的源头配置，
     * 可以是这样的URL形式，也可以是符合Swagger规范的String类型或者从文件中读取的流。
     * 如果是对当前使用的Swagger项目，我们通过使用访问本地Swagger接口的方式，
     * 如果是从外部获取的Swagger文档配置文件，就可以通过字符串或读文件的方式
     * <p>
     * toFolder(Paths.get("src/docs/asciidoc/generated")：指定最终生成文件的具体目录位置
     *
     * @throws URISyntaxException
     */
    @Test
    public void generateAsciiDocs() throws URISyntaxException, InterruptedException {
        //输出 ascii 格式
        Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
                .withMarkupLanguage(MarkupLanguage.ASCIIDOC)
                .build();

        /**
         * 如果不想分割结果文件，也可以通过替换
         * toFolder(Paths.get("src/docs/asciidoc/generated")为
         * toFile(Paths.get("src/docs/asciidoc/generated/all"))，
         * 将转换结果输出到一个单一的文件中，这样可以最终生成html的也是单一的。
         *
         */
        Swagger2MarkupConverter.from(new URI("http://localhost:18081/v2/api-docs"))
                .withConfig(config)
                .build()
                .toFile(Paths.get("/src/docs/assiidoc/generated/all"));

    }
}
