package core.consoleui.actions;

import core.consoleui.page.SynonymsPage;
import lib.constants.UnbxdErrorConstants;
import org.fluentlenium.core.domain.FluentWebElement;
import org.testng.Assert;

import static lib.constants.UnbxdErrorConstants.SUCCESS_MESSAGE_FAILURE;

public class ContentActions extends SynonymsPage {



    public String createStemming() throws InterruptedException {
        String stem="autoStem"+ System.currentTimeMillis();
        String stemmed="autoStemmed"+ System.currentTimeMillis();

        Thread.sleep(10000);
        awaitForElementPresence(synonymCreationButton);
        click(synonymCreationButton);
        awaitForElementPresence(synonymCreateWindow);
        await();
        Assert.assertTrue(awaitForElementPresence(synonymCreateWindow));

        synonymInput.fill().with(stem);
        await();
        stemmedWordInput.fill().with(stemmed);

        awaitForElementPresence(createSynonymButton);
        createSynonymButton.click();
        awaitForElementNotDisplayed(synonymCreateWindow);
        return stem;
    }

    public FluentWebElement getSynonymByIndex(int index)
    {
        return createdItemList.get(0);
    }

    public FluentWebElement getKeyWordsByName(String name) throws InterruptedException
    {
        Thread.sleep(2000);
        click(searchBoxOpen);
        Thread.sleep(10000);
        synonymsSearchBox.clear();
        synonymsSearchBox.fill().with(name);
        Thread.sleep(10000);
        awaitForElementPresence(synonymsLists);
        for (int i = 0; i< createdItemList.size(); i++)
        {
            if(createdItemList.get(i).find(keywordCssLocator).getValue().trim().equals(name))
            {
                Thread.sleep(10000);
                return createdItemList.get(i);
            }
        }
        return null;
    }

    public void selectCount()
    {
        awaitForElementPresence(countDropDown);
        click(countDropDown);
        awaitForElementPresence(selectCount100);
        click(selectCount100);
    }

    public FluentWebElement getUniDirectionalSynonym(FluentWebElement synonym)
    {
        return synonym.findFirst(unidirectionalCssLocator);
    }

    public FluentWebElement getStemmedWord(FluentWebElement stem)
    {
        return stem.findFirst(stemmedWord);
    }


    public FluentWebElement getBiDirectionalSynonym(FluentWebElement synonym)
    {
        return synonym.findFirst(biDirectionalCssLocator);
    }

    public FluentWebElement getKeyWord(FluentWebElement synonym)
    {
        await();
        return synonym.findFirst(keywordCssLocator);
    }



    public void saveChanges()
    {
        awaitForElementPresence(saveChangesButton);
        saveChangesButton.click();
    }

    public int getSynonymsSize(String keyword) throws InterruptedException {
        Thread.sleep(4000);
        if(createdItemList.size()==0)
            return 0;
        if(!searchBoxOpen.getAttribute("class").contains("hide")) {

            searchBoxOpen.click();
        }

        synonymsSearchBox.fill().with(keyword);
        awaitForElementNotDisplayed(PageLoader);
        int size= createdItemList.size();

        synonymsSearchBox.clear();
        awaitForElementNotDisplayed(PageLoader);

        return size;

    }



    public void createSynonym(String keyword, String unidirectional, String bidirectional) {
        awaitForElementPresence(synonymCreationButton);
        synonymCreationButton.click();
        
        awaitForElementPresence(synonymCreateWindow);
        awaitForElementPresence(synonymInput);
        synonymInput.fill().with(keyword);
        
        awaitForElementPresence(unidirections);
        awaitForElementPresence(unidirectionalInput);
        unidirectionalInput.fill().with(unidirectional);
        
        awaitForElementPresence(bidirections);
        awaitForElementPresence(bidirectionalInput);
        bidirectionalInput.fill().with(bidirectional);
        
        awaitForElementPresence(createSynonymButton);
        createSynonymButton.click();
        
        // Wait for the window to disappear
        awaitForElementNotDisplayed(synonymCreateWindow);
    }

}
