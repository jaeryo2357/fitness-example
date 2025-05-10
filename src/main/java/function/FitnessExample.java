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
                    WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
                    if (suiteSetup != null) {
                        includePage(suiteSetup, setup);
                    }
                }
                String pageName2 = "SetUp";
                String setup2 = "!include -setup .";
                WikiPage setup = PageCrawlerImpl.getInheritedPage(pageName2, wikiPage);
                if (setup != null) {
                    includePage(setup, setup2);
                }
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                String pageName3 = "TearDown";
                String teardown1 = "!include -teardown .";
                WikiPage teardown = PageCrawlerImpl.getInheritedPage(pageName3, wikiPage);
                if (teardown != null) {
                    includePage(teardown, teardown1);
                }
                if (includeSuiteSetup) {
                    String pageName4 = SuiteResponder.SUITE_TEARDOWN_NAME;
                    String teardown2 = "!include -teardown .";
                    WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(pageName4, wikiPage);
                    if (suiteTeardown != null) {
                        includePage(suiteTeardown, teardown2);
                    }
                }
            }

            pageData.setContent(buffer.toString());
            return pageData.getHtml();
        }

        private void includePage(WikiPage suiteSetup, String setup) {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
            String pagePathName = PathParser.render(pagePath);
            buffer.append(setup).append(pagePathName).append("\n");
        }
    }
}
