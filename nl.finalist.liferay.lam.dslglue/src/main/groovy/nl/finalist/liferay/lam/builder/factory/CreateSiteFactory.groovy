package nl.finalist.liferay.lam.builder.factory

import nl.finalist.liferay.lam.api.model.PageModel
import nl.finalist.liferay.lam.dslglue.LocaleMapConverter
import nl.finalist.liferay.lam.api.Site;
import nl.finalist.liferay.lam.dslglue.model.SiteModel;

class CreateSiteFactory extends AbstractFactory {

    Site siteService;

    CreateSiteFactory(Site site) {
        this.siteService = site;
    }

    @Override
    Object newInstance(FactoryBuilderSupport builder, Object objectName, Object value, Map attributes)
                    throws InstantiationException, IllegalAccessException {
        new SiteModel(attributes);
    }

    @Override
    void onNodeCompleted(FactoryBuilderSupport builder, Object parent, Object node) {
        super.onNodeCompleted(builder, parent, node);
        SiteModel model = (SiteModel) node;
        List<Map> pageMapList = model.pages;
        List<PageModel> pages = new ArrayList<>();
        for (Map pageMap: pageMapList) {
            PageModel page = new PageModel(
                pageMap.get("privatePage"),
                LocaleMapConverter.convert(pageMap.get("nameMap")),
                LocaleMapConverter.convert(pageMap.get("titleMap")),
                LocaleMapConverter.convert(pageMap.get("descriptionMap")),
                LocaleMapConverter.convert(pageMap.get("friendlyUrlMap")),
                pageMap.get("typeSettings"));
            pages.add(page);
        }
        siteService.addSite(LocaleMapConverter.convert(model.nameMap), LocaleMapConverter.convert(model.descriptionMap), model.friendlyURL, model.customFields, pages);
    }
}