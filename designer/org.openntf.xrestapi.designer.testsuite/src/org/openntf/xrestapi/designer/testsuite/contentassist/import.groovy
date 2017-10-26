import java.util.Map;
import org.openntf.xrestapi.designer.testsuite.mock.MyTestMock
import org.markdown4j.Markdown4jProcessor;


Markdown4jProcessor processor = new Markdown4jProcessor();
MyTestMock mock = new MyTestMock();
processor.process("testString");
def blubbi = mock.getAllInfos();
mock.g