package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {
    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        return new TestableHtmlBuilder(pageData, includeSuiteSetup).invoke();
    }

    private class TestableHtmlBuilder {
        private PageData pageData;
        private boolean includeSuiteSetup;
        private WikiPage wikiPage;
        private StringBuffer buffer;

        public TestableHtmlBuilder(PageData pageData, boolean includeSuiteSetup) {
            this.pageData = pageData;
            this.includeSuiteSetup = includeSuiteSetup;
            this.wikiPage = pageData.getWikiPage();
            this.buffer = new StringBuffer();
        }

        public String invoke() {

            if (pageData.hasAttribute("Test")) {
                if (includeSuiteSetup) {
                    String pageName = SuiteResponder.SUITE_SETUP_NAME;
                    String setup = "!include -setup .";
                    includeInherited(pageName, setup);
                }
                String pageName2 = "SetUp";
                String setup2 = "!include -setup .";
                includeInherited(pageName2, setup2);
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                String pageName3 = "TearDown";
                String teardown1 = "!include -teardown .";
                includeInherited(pageName3, teardown1);
                if (includeSuiteSetup) {
                    String pageName4 = SuiteResponder.SUITE_TEARDOWN_NAME;
                    String teardown2 = "!include -teardown .";
                    includeInherited(pageName4, teardown2);
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includeInherited(String pageName, String setup) {
            WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
            if (suiteSetup != null) {
                includePage(suiteSetup, setup);
            }
        }

        private void includePage(WikiPage suiteSetup, String setup) {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
            String pagePathName = PathParser.render(pagePath);
            buffer.append(setup).append(pagePathName).append("\n");
        }
    }
}
