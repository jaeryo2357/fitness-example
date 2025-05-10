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

            if (isTestPage())
                surroundPageWithSetUpsAndTearDowns();
            return pageData.getHtml();
        }

        private void surroundPageWithSetUpsAndTearDowns() {
            includeSetups();
            buffer.append(pageData.getContent());
            includeTearDowns();
            pageData.setContent(buffer.toString());
        }

        private boolean isTestPage() {
            return pageData.hasAttribute("Test");
        }

        private void includeTearDowns() {
            includeInherited("TearDown", "!include -teardown .");
            if (includeSuiteSetup)
                includeInherited(SuiteResponder.SUITE_TEARDOWN_NAME, "!include -teardown .");
        }

        private void includeSetups() {
            if (includeSuiteSetup)
                includeInherited(SuiteResponder.SUITE_SETUP_NAME, "!include -setup .");
            includeInherited("SetUp", "!include -setup .");
        }

        private void includeInherited(String pageName, String setup) {
            WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
            if (suiteSetup != null)
                includePage(suiteSetup, setup);
        }

        private void includePage(WikiPage suiteSetup, String setup) {
            WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
            String pagePathName = PathParser.render(pagePath);
            buffer.append(setup).append(pagePathName).append("\n");
        }
    }
}
