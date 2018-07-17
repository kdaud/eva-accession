/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.eva.accession.core;

import uk.ac.ebi.ampt2d.commons.accession.core.AccessionVersionsWrapper;
import uk.ac.ebi.ampt2d.commons.accession.core.AccessionWrapper;
import uk.ac.ebi.ampt2d.commons.accession.core.AccessioningService;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionCouldNotBeGeneratedException;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionDeprecatedException;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionDoesNotExistException;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.AccessionMergedException;
import uk.ac.ebi.ampt2d.commons.accession.core.exceptions.HashAlreadyExistsException;
import uk.ac.ebi.ampt2d.commons.accession.generators.monotonic.MonotonicAccessionGenerator;
import uk.ac.ebi.ampt2d.commons.accession.hashing.SHA1HashingFunction;
import uk.ac.ebi.ampt2d.commons.accession.persistence.jpa.monotonic.service.MonotonicDatabaseService;
import uk.ac.ebi.ampt2d.commons.accession.service.BasicMonotonicAccessioningService;

import uk.ac.ebi.eva.accession.core.summary.SubmittedVariantSummaryFunction;

import java.util.List;
import java.util.stream.Collectors;

public class SubmittedVariantAccessioningService implements AccessioningService<ISubmittedVariant, String, Long> {

    private static final Long ACCESSIONING_MONOTONIC_INIT_SS = 5000000000L;

    private BasicMonotonicAccessioningService<ISubmittedVariant, String> accessioningService;

    private BasicMonotonicAccessioningService<ISubmittedVariant, String> accessioningServiceDbsnp;

    public SubmittedVariantAccessioningService(MonotonicAccessionGenerator<ISubmittedVariant> accessionGenerator,
                                               MonotonicDatabaseService dbService,
                                               MonotonicDatabaseService dbServiceDbsnp) {
        this.accessioningService = new BasicMonotonicAccessioningService<ISubmittedVariant, String>
                (accessionGenerator, dbService, new SubmittedVariantSummaryFunction(), new SHA1HashingFunction());
        this.accessioningServiceDbsnp = new BasicMonotonicAccessioningService<ISubmittedVariant, String>
                (accessionGenerator, dbServiceDbsnp, new SubmittedVariantSummaryFunction(), new SHA1HashingFunction());
    }

    @Override
    public List<AccessionWrapper<ISubmittedVariant, String, Long>> getOrCreate(
            List<? extends ISubmittedVariant> variants)
            throws AccessionCouldNotBeGeneratedException {
        List<AccessionWrapper<ISubmittedVariant, String, Long>> dbsnpVariants = accessioningServiceDbsnp.get(variants);
        List<ISubmittedVariant> variantsNotInDbsnp = removeFromList(variants, dbsnpVariants);
        List<AccessionWrapper<ISubmittedVariant, String, Long>> submittedVariants = accessioningService.getOrCreate(variantsNotInDbsnp);
        return joinLists(submittedVariants, dbsnpVariants);
    }

    private List<ISubmittedVariant> removeFromList(List<? extends ISubmittedVariant> allVariants,
                                                   List<AccessionWrapper<ISubmittedVariant, String, Long>>
                                                           vatiantsToDelete) {
        return allVariants.stream().filter(variant -> !contains(vatiantsToDelete, variant))
                          .collect(Collectors.toList());
    }

    private Boolean contains(List<AccessionWrapper<ISubmittedVariant, String, Long>> accessionWrapperList,
                             ISubmittedVariant iSubmittedVariant) {
        return accessionWrapperList.stream().anyMatch(x -> x.getData().equals(iSubmittedVariant));
    }

    private List<AccessionWrapper<ISubmittedVariant, String, Long>> joinLists(List l1, List l2) {
        List allVariants = l1;
        allVariants.addAll(l2);
        return allVariants;
    }

    @Override
    public List<AccessionWrapper<ISubmittedVariant, String, Long>> get(List<? extends ISubmittedVariant> variants) {
        return joinLists(accessioningService.get(variants), accessioningServiceDbsnp.get(variants));
    }

    @Override
    public List<AccessionWrapper<ISubmittedVariant, String, Long>> getByAccessions(List<Long> accessions) {
        return joinLists(accessioningService.getByAccessions(accessions),
                         accessioningServiceDbsnp.getByAccessions(accessions));
    }

    @Override
    public AccessionWrapper<ISubmittedVariant, String, Long> getByAccessionAndVersion(Long accession, int version)
            throws AccessionDoesNotExistException, AccessionMergedException, AccessionDeprecatedException {
        if (accession >= ACCESSIONING_MONOTONIC_INIT_SS) {
            return accessioningService.getByAccessionAndVersion(accession, version);
        } else {
            return accessioningServiceDbsnp.getByAccessionAndVersion(accession, version);
        }
    }

    @Override
    public AccessionVersionsWrapper<ISubmittedVariant, String, Long> update(Long accession, int version,
                                                                            ISubmittedVariant iSubmittedVariant)
            throws AccessionDoesNotExistException, HashAlreadyExistsException, AccessionDeprecatedException,
            AccessionMergedException {
        return accessioningService.update(accession, version, iSubmittedVariant);
    }

    @Override
    public AccessionVersionsWrapper<ISubmittedVariant, String, Long> patch(Long accession, ISubmittedVariant variant)
            throws AccessionDoesNotExistException, HashAlreadyExistsException, AccessionDeprecatedException,
            AccessionMergedException {
        return accessioningService.patch(accession, variant);
    }

    @Override
    public void deprecate(Long accession, String reason)
            throws AccessionMergedException, AccessionDoesNotExistException, AccessionDeprecatedException {
        accessioningService.deprecate(accession, reason);
    }

    @Override
    public void merge(Long accession, Long accession1, String reason)
            throws AccessionMergedException, AccessionDoesNotExistException, AccessionDeprecatedException {
        accessioningService.merge(accession, accession1, reason);
    }
}