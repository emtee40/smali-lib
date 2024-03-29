/*
 * Copyright 2013, Google Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following disclaimer
 * in the documentation and/or other materials provided with the
 * distribution.
 *     * Neither the name of Google Inc. nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jf.dexlib2.writer.builder;

import org.jf.dexlib2.base.reference.BaseTypeReference;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.util.MethodUtil;
import org.jf.dexlib2.writer.DexWriter;
import org.jf.dexlib2.writer.builder.BuilderEncodedValues.BuilderArrayEncodedValue;
import org.jf.util.collection.ArraySet;
import org.jf.util.collection.Iterables;
import org.jf.util.collection.ListUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class BuilderClassDef extends BaseTypeReference implements ClassDef {
    @Nonnull final BuilderTypeReference type;
    final int accessFlags;
    @Nullable final BuilderTypeReference superclass;
    @Nonnull final BuilderTypeList interfaces;
    @Nullable final BuilderStringReference sourceFile;
    @Nonnull final BuilderAnnotationSet annotations;
    @Nonnull final Set<BuilderField> staticFields;
    @Nonnull final Set<BuilderField> instanceFields;
    @Nonnull final Set<BuilderMethod> directMethods;
    @Nonnull final Set<BuilderMethod> virtualMethods;
    @Nullable final BuilderArrayEncodedValue staticInitializers;

    int classDefIndex = DexWriter.NO_INDEX;
    int annotationDirectoryOffset = DexWriter.NO_OFFSET;

    BuilderClassDef(@Nonnull BuilderTypeReference type,
                    int accessFlags,
                    @Nullable BuilderTypeReference superclass,
                    @Nonnull BuilderTypeList interfaces,
                    @Nullable BuilderStringReference sourceFile,
                    @Nonnull BuilderAnnotationSet annotations,
                    @Nullable Set<BuilderField> staticFields,
                    @Nullable Set<BuilderField> instanceFields,
                    @Nullable Iterable<? extends BuilderMethod> methods,
                    @Nullable BuilderArrayEncodedValue staticInitializers) {
        if (methods == null) {
            methods = ListUtil.of();
        }
        if (staticFields == null) {
            staticFields = ArraySet.of();
        }
        if (instanceFields == null) {
            instanceFields = ArraySet.of();
        }

        this.type = type;
        this.accessFlags = accessFlags;
        this.superclass = superclass;
        this.interfaces = interfaces;
        this.sourceFile = sourceFile;
        this.annotations = annotations;
        this.staticFields = staticFields;
        this.instanceFields = instanceFields;
        ArraySet<BuilderMethod> set = ArraySet.copyOf(Iterables.filter(methods, MethodUtil.METHOD_IS_DIRECT));
        this.directMethods = set.sort();
        set=ArraySet.copyOf(Iterables.filter(methods, MethodUtil.METHOD_IS_VIRTUAL));;
        this.virtualMethods = set.sort();
        this.staticInitializers = staticInitializers;
    }

    @Nonnull
    @Override
    public String getType() { return type.getType(); }
    @Override
    public int getAccessFlags() { return accessFlags; }
    @Nullable
    @Override
    public String getSuperclass() { return superclass==null?null:superclass.getType(); }
    @Nullable
    @Override
    public String getSourceFile() { return sourceFile==null?null:sourceFile.getString(); }
    @Nonnull
    @Override
    public BuilderAnnotationSet getAnnotations() { return annotations; }
    @Nonnull
    @Override
    public Set<BuilderField> getStaticFields() { return staticFields; }
    @Nonnull
    @Override
    public Set<BuilderField> getInstanceFields() { return instanceFields; }
    @Nonnull
    @Override
    public Set<BuilderMethod> getDirectMethods() { return directMethods; }
    @Nonnull
    @Override
    public Set<BuilderMethod> getVirtualMethods() { return virtualMethods; }

    @Nonnull
    @Override
    public List<String> getInterfaces() {
        return ListUtil.transform(this.interfaces, new Function<BuilderTypeReference, String>() {
            @Override
            public String apply(BuilderTypeReference builderTypeReference) {
                return builderTypeReference.toString();
            }
        });
    }

    @Nonnull
    @Override
    public Collection<BuilderField> getFields() {
        ArraySet<BuilderField> results = new ArraySet<>(staticFields.size() + instanceFields.size());
        results.addAll(staticFields);
        results.addAll(instanceFields);
        results.sort();
        return results;
    }

    @Nonnull
    @Override
    public Collection<BuilderMethod> getMethods() {
        ArraySet<BuilderMethod> results = new ArraySet<>(directMethods.size() + virtualMethods.size());
        results.addAll(directMethods);
        results.addAll(virtualMethods);
        results.sort();
        return results;
    }
}
