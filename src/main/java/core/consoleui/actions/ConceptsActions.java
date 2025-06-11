package core.consoleui.actions;

import core.consoleui.page.ConceptsPage;
import lib.constants.UnbxdErrorConstants;
import org.fluentlenium.core.domain.FluentWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;

import static lib.constants.UnbxdErrorConstants.SUCCESS_MESSAGE_FAILURE;

public class ConceptsActions extends ConceptsPage {

    public String createKeyword() throws InterruptedException
    {
        String keyword = "keyword" + System.currentTimeMillis();

        Thread.sleep(10000);
        awaitForElementPresence(synonymCreationButton);
        click(synonymCreationButton);
        awaitForElementPresence(synonymCreateWindow);
        await();
        Assert.assertTrue(awaitForElementPresence(synonymCreateWindow));
        conceptsKeyWordInput.fill().with(keyword);
        await();
        createSynonymButton.click();
        awaitForElementNotDisplayed(synonymCreateWindow);
        //Assert.assertTrue(checkSuccessMessage(), UnbxdErrorConstants.SUCCESS_MESSAGE_FAILURE);
        return keyword;
    }

    public FluentWebElement getConceptsByIndex(int index)
    {
        return itemList.get(0);
    }

    public int getConceptsSize(String keyword) throws InterruptedException {
        Thread.sleep(4000);
        synonymsSearchBox.clear();
        awaitForElementNotDisplayed(PageLoader);

        if(itemList.size()==0)
            return 0;

        int size=itemList.size();
        return size;
    }

    public FluentWebElement getKeyWord(FluentWebElement concepts)
    {
        return concepts.findFirst(keywordLocator);
    }

    public void deleteKeyword(String name) throws InterruptedException {
        FluentWebElement synonym=getKeywordByName(name);
        Thread.sleep(6000);
        synonym.findFirst(conceptsCloseButton).click();
        Assert.assertTrue(awaitForElementPresence(deleteConfirmationWindow),"Delete Confirmation Window is not getting Opened");
        deleteYesSynonymButton.click();
        awaitForElementNotDisplayed(PageLoader);
        Assert.assertTrue(checkSuccessMessage(),SUCCESS_MESSAGE_FAILURE);
    }

    public FluentWebElement getKeywordByName(String name) throws InterruptedException {
        Thread.sleep(2000);
        click(searchBoxOpen);
        Thread.sleep(6000);
        synonymsSearchBox.fill().with(name);
        Thread.sleep(6000);
        awaitForElementPresence(itemLists);
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getText().trim().equals(name)) {
                Thread.sleep(10000);
                return itemList.get(i);
            }
        }
        return null;
    }

}
