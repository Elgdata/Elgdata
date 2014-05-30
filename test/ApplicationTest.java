import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;

import org.junit.Test;

import play.mvc.Content;

/**
 * 
 * Simple (JUnit) tests that can call all parts of a play app. If you are
 * interested in mocking a whole application, see the wiki for more details.
 * 
 */
public class ApplicationTest {
	
	@Test
	public void simpleChecks() {
		int a = 1 + 1;
		assertThat(a).isEqualTo(2);
		
		assertThat(true).isEqualTo(true);
		assertThat(false).isFalse();
	}
	
	@Test
	public void renderHomePage() {
		
		Content html = views.html.homepage.index.render("findThisString");
		assertThat(contentType(html)).isEqualTo("text/html");
		assertThat(contentAsString(html)).contains("findThisString");
	}
	
//	 @Test
//	 @Transactional(readOnly = true)
//	 public void testDatabaseConnection(){
//	
//	 assertThat(JPA.em()).isNotNull();
//	
//	 Elk testElk = JPA.em()
//	 .createQuery("from Elk as elk", Elk.class)
//	 .getSingleResult();
//	
//	 assertThat(testElk).isNotNull();
//	 assertThat(testElk).isInstanceOf(Elk.class);
//	 }
//
//    @Test
//    public void findById() {
//        running(fakeApplication(), new Runnable() {
//           public void run() {
//               JPA.withTransaction(new play.libs.F.Callback0() {
//                   public void invoke() {
//                       Computer macintosh = Computer.findById(21l);
//                       assertThat(macintosh.name).isEqualTo("Macintosh");
//                       assertThat(formatted(macintosh.introduced)).isEqualTo("1984-01-24");
//                   }
//               });
//           }
//        });
//    }
}
