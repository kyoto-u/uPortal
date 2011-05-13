package org.jasig.portal.portlet.container.properties;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portal.IUserProfile;
import org.jasig.portal.UserPreferencesManager;
import org.jasig.portal.layout.dao.IStylesheetDescriptorDao;
import org.jasig.portal.layout.om.IStylesheetDescriptor;
import org.jasig.portal.portlet.om.IPortletWindow;
import org.jasig.portal.user.IUserInstance;
import org.jasig.portal.user.IUserInstanceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

@Service
public class ThemeNameRequestPropertiesManager extends BaseRequestPropertiesManager {
    
    public static final String THEME_NAME_PROPERTY = "themeName";

    private IUserInstanceManager userInstanceManager;

    @Autowired
    public void setUserInstanceManager(IUserInstanceManager userInstanceManager) {
        this.userInstanceManager = userInstanceManager;
    }
    
    private IStylesheetDescriptorDao stylesheetDao;
    
    @Autowired
    public void setStylesheetDescriptorDao(IStylesheetDescriptorDao stylesheetDao) {
        this.stylesheetDao = stylesheetDao;
    }

    @Override
    public Map<String, String[]> getRequestProperties(
            HttpServletRequest portletRequest, IPortletWindow portletWindow) {
        
        // get the current user profile
        IUserInstance ui = userInstanceManager.getUserInstance(portletRequest);
        UserPreferencesManager upm = (UserPreferencesManager) ui.getPreferencesManager();
        IUserProfile profile = upm.getUserProfile();
        
        // get the theme for this profile
        long themeId = profile.getThemeStylesheetId();
        IStylesheetDescriptor theme = stylesheetDao.getStylesheetDescriptor(themeId);

        // set the theme name as a portlet response property
        return Collections.singletonMap(THEME_NAME_PROPERTY, new String[]{ theme.getName() });
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
