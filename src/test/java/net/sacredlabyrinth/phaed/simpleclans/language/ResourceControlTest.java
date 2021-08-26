package net.sacredlabyrinth.phaed.simpleclans.language;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class ResourceControlTest {

    @Test
    public void getFallbackLocaleAcf() {
        LanguageResource.ResourceControl resourceControl = new LanguageResource.ResourceControl(Locale.ENGLISH,
                true);
        String acfBaseName = "acf";
        Locale locale = resourceControl.getFallbackLocale(acfBaseName, Locale.ENGLISH);
        Assert.assertNull(locale);
        locale = resourceControl.getFallbackLocale(acfBaseName, new Locale("es", "ES"));
        Assert.assertEquals(Locale.ENGLISH, locale);

        Locale ptBR = new Locale("pt", "BR");
        resourceControl = new LanguageResource.ResourceControl(ptBR, true);
        locale = resourceControl.getFallbackLocale(acfBaseName, Locale.ENGLISH);
        Assert.assertNull(locale);
        locale = resourceControl.getFallbackLocale(acfBaseName, ptBR);
        Assert.assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    public void getFallbackLocale() {
        LanguageResource.ResourceControl resourceControl = new LanguageResource.ResourceControl(Locale.ENGLISH,
                true);
        String pluginBaseName = "messages";
        Locale locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ENGLISH);
        Assert.assertEquals(Locale.ROOT, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ROOT);
        Assert.assertNull(locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.CANADA);
        Assert.assertNotSame(Locale.CANADA, locale);

        Locale ptBR = new Locale("pt", "BR");
        resourceControl = new LanguageResource.ResourceControl(ptBR, true);
        locale = resourceControl.getFallbackLocale(pluginBaseName, ptBR);
        Assert.assertEquals(Locale.ROOT, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ROOT);
        Assert.assertNull(locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.CANADA);
        Assert.assertNotSame(Locale.CANADA, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ENGLISH);
        Assert.assertEquals(ptBR, locale);
    }
}