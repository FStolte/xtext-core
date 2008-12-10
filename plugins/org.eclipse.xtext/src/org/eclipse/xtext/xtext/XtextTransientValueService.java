/*******************************************************************************
 * Copyright (c) 2008 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.xtext.xtext;

import java.util.List;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.xtext.AbstractMetamodelDeclaration;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.GeneratedMetamodel;
import org.eclipse.xtext.Grammar;
import org.eclipse.xtext.GrammarUtil;
import org.eclipse.xtext.LexerRule;
import org.eclipse.xtext.ParserRule;
import org.eclipse.xtext.TypeRef;
import org.eclipse.xtext.XtextPackage;
import org.eclipse.xtext.parsetree.reconstr.impl.SimpleTransientValueService;
import org.eclipse.xtext.resource.metamodel.DeclaredMetamodelAccessFactory;
import org.eclipse.xtext.resource.metamodel.IDeclaredMetamodelAccess;
import org.eclipse.xtext.util.Strings;

/**
 * @author Moritz Eysholdt - Initial contribution and API
 * @author Sebastian Zarnekow
 */
public class XtextTransientValueService extends SimpleTransientValueService {

	public boolean isTransient(EObject owner, EStructuralFeature feature,
			int index) {
		if (feature == XtextPackage.eINSTANCE.getTypeRef_Metamodel()) {
			final TypeRef typeRef = (TypeRef) owner;
			final AbstractMetamodelDeclaration m = typeRef.getMetamodel();
			if (m == null || Strings.isEmpty(m.getAlias()))
				return true;
			if (typeRef.getType() != null) {
				final Grammar g = GrammarUtil.getGrammar(typeRef);
				if (isUniqueEClassifierName(g, typeRef.getType().getName()))
					return true;
			}
		}
		else if (feature == XtextPackage.eINSTANCE.getAbstractRule_Type()) {
			final AbstractRule rule = (AbstractRule) owner;
			if (rule instanceof ParserRule) {
				final TypeRef returnType = rule.getType();
				if (returnType.getType() != null && rule.getName().equals(returnType.getType().getName())) {
					return isTransient(returnType, XtextPackage.eINSTANCE.getTypeRef_Metamodel(), -1);
				}
			} else if (rule instanceof LexerRule) {
				final TypeRef returnType = rule.getType();
				return returnType == null || EcorePackage.eINSTANCE.getEString().equals(rule.getType().getType());
			}
		}
		return super.isTransient(owner, feature, index);
	}
	
	private boolean isUniqueEClassifierName(Grammar grammar, String shortTypeName) {
		final List<AbstractMetamodelDeclaration> declarations = GrammarUtil.allMetamodelDeclarations(grammar);
		AbstractMetamodelDeclaration resultMetaModel = null;
		int generatedMetamodelCount = 0;
		for (AbstractMetamodelDeclaration decl : declarations) {
			if (decl instanceof GeneratedMetamodel)
				generatedMetamodelCount++;
			IDeclaredMetamodelAccess access = DeclaredMetamodelAccessFactory.getAccessTo(decl);
			EClassifier candidate = access.getEClassifier(shortTypeName);
			if (candidate != null) {
				if (resultMetaModel == null) {
					resultMetaModel = decl;
				} else {
					return false;
				}
			}
		}
		return !(resultMetaModel instanceof GeneratedMetamodel && generatedMetamodelCount > 1);
	}
}
