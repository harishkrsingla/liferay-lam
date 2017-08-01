package nl.finalist.liferay.lam.api;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringPool;

import nl.finalist.liferay.lam.api.model.PageModel;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ LocaleUtil.class, SiteImpl.class, PropsUtil.class, Locale.class, PortalUtil.class })
public class SiteImplTest {

	private static final long SITE_ID = 123L;
	private static final long USER_ID = 10L;
	private static final long COMPANY_ID = 1L;
	@Mock
	private CompanyLocalService companyService;
	@Mock
	private GroupLocalService siteService;
	@Mock
	private UserLocalService userService;
	@Mock
	private CounterLocalService counterService;
	@Mock
	private Company mockCompany;
	@Mock
	private Group mockSite;
	@Mock
	private User mockDefaultUser;
	@Mock
	private HashMap<Locale, String> mockTitleMap;
	@Mock
	private ServiceContext mockServiceContext;
	@Mock
	private CustomFields customFieldsService;
	@Mock
	private Page pageService;
	@Mock
	private Layout pageLayout;
	@InjectMocks
	private SiteImpl siteImpl;

	Map<Locale, String> nameMap;
	Map<Locale, String> descriptionMap;
	String friendlyURL;
	String siteKey;

	@Before
	public void setUp() throws PortalException {
		siteImpl = new SiteImpl();
		PowerMockito.mockStatic(LocaleUtil.class);
		PowerMockito.mockStatic(PropsUtil.class);
		PowerMockito.mockStatic(PortalUtil.class);
		PowerMockito.when(PropsUtil.get("company.default.web.id")).thenReturn("liferay.com");
		PowerMockito.when(PortalUtil.getDefaultCompanyId()).thenReturn(COMPANY_ID);
		initMocks(this);

		siteKey = "testName";
		nameMap = new HashMap<>();
		nameMap.put(Locale.US, siteKey);
		descriptionMap = new HashMap<>();
		descriptionMap.put(Locale.US, "testDescription");
		friendlyURL = "/test";

	}

	@Test
	public void testAddSite() throws Exception {
		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		Locale mockLocale = new Locale("en_US");
		PowerMockito.when(LocaleUtil.getSiteDefault()).thenReturn(mockLocale);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);
		siteImpl.addSite(nameMap, descriptionMap, friendlyURL, null, null);

		verify(siteService).addGroup(USER_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0L,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true, false, true, null);
	}

	@Test
	public void testAddSiteWithCustomField() throws Exception {
		Map<String, String> customFields = createCustomFields();

		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		Locale mockLocale = new Locale("en_US");
		PowerMockito.when(LocaleUtil.getSiteDefault()).thenReturn(mockLocale);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);

		when(siteService.addGroup(USER_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0L,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true, false, true, null))
						.thenReturn(mockSite);
		when(mockSite.getPrimaryKey()).thenReturn(1L);

		siteImpl.addSite(nameMap, descriptionMap, friendlyURL, customFields, null);

		verify(siteService).addGroup(USER_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0L,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true, false, true, null);
		verify(customFieldsService).addCustomFieldValue(Group.class.getName(), "someField", 1L, "someValue");
	}

	@Test
	public void testAddSiteWithPages() throws Exception {
		List<PageModel> pages = createPageModel();

		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		Locale mockLocale = new Locale("en_US");
		PowerMockito.when(LocaleUtil.getSiteDefault()).thenReturn(mockLocale);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);

		when(siteService.addGroup(USER_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0L,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true, false, true, null))
						.thenReturn(mockSite);
		when(mockSite.getGroupId()).thenReturn(1L);

		siteImpl.addSite(nameMap, descriptionMap, friendlyURL, null, pages);

		verify(siteService).addGroup(USER_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, Group.class.getName(), 0L,
				GroupConstants.DEFAULT_LIVE_GROUP_ID, nameMap, descriptionMap, GroupConstants.TYPE_SITE_OPEN, true,
				GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, true, false, true, null);
	}

	@Test
	public void testDeleteExistingSite() throws PortalException {
		when(siteService.getGroup(COMPANY_ID, siteKey)).thenReturn(mockSite);
		when(mockSite.getGroupId()).thenReturn(SITE_ID);

		siteImpl.deleteSite(siteKey);

		verify(siteService).deleteGroup(SITE_ID);
	}

	@Test
	public void testUpdateSiteTranslation() throws PortalException {
		when(siteService.getGroup(COMPANY_ID, siteKey)).thenReturn(mockSite);
		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);

		when(mockSite.getGroupId()).thenReturn(SITE_ID);
		List<PageModel> pages = createPageModel();
		siteImpl.updateSite(siteKey, nameMap, descriptionMap, friendlyURL, null, pages);

		verify(siteService).updateGroup(SITE_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, nameMap, descriptionMap,
				GroupConstants.TYPE_SITE_OPEN, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, false,
				true, null);
	}

	@Test
	public void testUpdateSiteTranslationWithCustomFields() throws PortalException {

		when(siteService.getGroup(COMPANY_ID, siteKey)).thenReturn(mockSite);
		when(mockSite.getGroupId()).thenReturn(SITE_ID);

		when(siteService.updateGroup(SITE_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, nameMap, descriptionMap,
				GroupConstants.TYPE_SITE_OPEN, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, false,
				true, null)).thenReturn(mockSite);
		when(mockSite.getPrimaryKey()).thenReturn(1L);

		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);

		siteImpl.updateSite(siteKey, nameMap, descriptionMap, friendlyURL, createCustomFields(), createPageModel());

		verify(siteService).updateGroup(SITE_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, nameMap, descriptionMap,
				GroupConstants.TYPE_SITE_OPEN, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, false,
				true, null);
		verify(customFieldsService).updateCustomFieldValue(Group.class.getName(), "someField", 1L, "someValue");
	}

	private Map<String, String> createCustomFields() {
		Map<String, String> customFields = new HashMap<>();
		customFields.put("someField", "someValue");
		return customFields;
	}

	@Test
	public void updatePageOfSite() throws PortalException {
		when(siteService.getGroup(COMPANY_ID, siteKey)).thenReturn(mockSite);

		when(mockSite.getGroupId()).thenReturn(SITE_ID);
		when(companyService.getCompanyByWebId("liferay.com")).thenReturn(mockCompany);
		when(mockCompany.getCompanyId()).thenReturn(COMPANY_ID);
		when(mockCompany.getDefaultUser()).thenReturn(mockDefaultUser);
		when(mockDefaultUser.getUserId()).thenReturn(USER_ID);

		when(siteService.updateGroup(SITE_ID, GroupConstants.DEFAULT_PARENT_GROUP_ID, nameMap, descriptionMap,
				GroupConstants.TYPE_SITE_OPEN, true, GroupConstants.DEFAULT_MEMBERSHIP_RESTRICTION, friendlyURL, false,
				true, null)).thenReturn(mockSite);
		
		pageLayout.setFriendlyURL(friendlyURL);
		pageLayout.setGroupId(SITE_ID);
		when(pageService.fetchLayout(SITE_ID, false, friendlyURL)).thenReturn(pageLayout);
		
		siteImpl.updateSite(siteKey, nameMap, descriptionMap, friendlyURL, createCustomFields(), createPageModel());
		
		verify(pageService).updatePage(anyLong(), anyLong(), any(PageModel.class));
		
	}

	private List<PageModel> createPageModel() {
		List<PageModel> pages = new ArrayList<>();
		PageModel page = createPage();
		pages.add(page);
		return pages;
	}

	private PageModel createPage() {
		Map<Locale, String> testMap = new HashMap<>();
		testMap.put(Locale.US, friendlyURL);
		PageModel page = new PageModel(true, nameMap, testMap, testMap, testMap, StringPool.BLANK);
		return page;
	}
}
