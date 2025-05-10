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
                    WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                    if (suiteSetup != null) {
                        String setup = "!include -setup .";
                        includePage(suiteSetup, setup);
                    }
                }
                WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
                if (setup != null) {
                    String setup2 = "!include -setup .";
                    includePage(setup, setup2);
                }
            }

            buffer.append(pageData.getContent());
            if (pageData.hasAttribute("Test")) {
                WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
                if (teardown != null) {
                    String teardown1 = "!include -teardown .";
                    includePage(teardown, teardown1);
                }
                if (includeSuiteSetup) {
                    WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                    if (suiteTeardown != null) {
                        String teardown2 = "!include -teardown .";
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
