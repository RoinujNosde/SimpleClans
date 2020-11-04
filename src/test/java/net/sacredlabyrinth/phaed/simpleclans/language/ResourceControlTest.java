package net.sacredlabyrinth.phaed.simpleclans.language;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.Assert.*;

class ResourceControlTest {

    @Test
    void getFallbackLocaleAcf() {
        LanguageResource.ResourceControl resourceControl = new LanguageResource.ResourceControl(Locale.ENGLISH,
                true);
        String acfBaseName = "acf";
        Locale locale = resourceControl.getFallbackLocale(acfBaseName, Locale.ENGLISH);
        assertNull(locale);
        locale = resourceControl.getFallbackLocale(acfBaseName, new Locale("es", "ES"));
        assertEquals(Locale.ENGLISH, locale);

        Locale ptBR = new Locale("pt", "BR");
        resourceControl = new LanguageResource.ResourceControl(ptBR, true);
        locale = resourceControl.getFallbackLocale(acfBaseName, Locale.ENGLISH);
        assertNull(locale);
        locale = resourceControl.getFallbackLocale(acfBaseName, ptBR);
        assertEquals(Locale.ENGLISH, locale);
    }

    @Test
    void getFallbackLocale() {
        LanguageResource.ResourceControl resourceControl = new LanguageResource.ResourceControl(Locale.ENGLISH,
                true);
        String pluginBaseName = "messages";
        Locale locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ENGLISH);
        assertEquals(Locale.ROOT, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ROOT);
        assertNull(locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.CANADA);
        assertNotSame(Locale.CANADA, locale);

        Locale ptBR = new Locale("pt", "BR");
        resourceControl = new LanguageResource.ResourceControl(ptBR, true);
        locale = resourceControl.getFallbackLocale(pluginBaseName, ptBR);
        assertEquals(Locale.ROOT, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ROOT);
        assertNull(locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.CANADA);
        assertNotSame(Locale.CANADA, locale);
        locale = resourceControl.getFallbackLocale(pluginBaseName, Locale.ENGLISH);
        assertEquals(ptBR, locale);
    }
}