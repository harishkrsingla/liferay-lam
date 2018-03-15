package nl.finalist.liferay.lam.builder;

import org.osgi.framework.Bundle;

import nl.finalist.liferay.lam.api.ADT
import nl.finalist.liferay.lam.api.Document
import nl.finalist.liferay.lam.api.Page
import nl.finalist.liferay.lam.api.Structure
import nl.finalist.liferay.lam.api.Template
import nl.finalist.liferay.lam.api.WebContent
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdateADTFactory
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdateDocumentFactory
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdatePageFactory
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdateStructureFactory
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdateTemplateFactory
import nl.finalist.liferay.lam.builder.factory.CreateOrUpdateWebcontentFactory

class CreateOrUpdateFactoryBuilder extends FactoryBuilderSupport {

    CreateOrUpdateFactoryBuilder(Structure structureService, Template templateService, ADT adtService, WebContent webContentService, , Page
        pageServiceDocument documentService, Bundle bundle){
        registerFactory("structure", new CreateOrUpdateStructureFactory(structureService, bundle));
        registerFactory("template", new CreateOrUpdateTemplateFactory(templateService, bundle));
        registerFactory("ADT", new CreateOrUpdateADTFactory(adtService, bundle));
        registerFactory("webcontent", new CreateOrUpdateWebcontentFactory(webContentService, bundle));
        registerFactory("page", new CreateOrUpdatePageFactory(pageService));
        registerFactory("document", new CreateOrUpdateDocumentFactory(documentService, bundle));
    }
}
