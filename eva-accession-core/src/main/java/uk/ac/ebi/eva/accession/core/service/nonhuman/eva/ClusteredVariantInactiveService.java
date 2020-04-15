/*
 *
 * Copyright 2020 EMBL - European Bioinformatics Institute
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

package uk.ac.ebi.eva.accession.core.service.nonhuman.eva;

import uk.ac.ebi.ampt2d.commons.accession.persistence.mongodb.service.BasicMongoDbInactiveAccessionService;
import uk.ac.ebi.ampt2d.commons.accession.persistence.repositories.IHistoryRepository;

import uk.ac.ebi.eva.accession.core.model.IClusteredVariant;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantEntity;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantInactiveEntity;
import uk.ac.ebi.eva.accession.core.model.eva.ClusteredVariantOperationEntity;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClusteredVariantInactiveService extends BasicMongoDbInactiveAccessionService<IClusteredVariant, Long,
        ClusteredVariantEntity, ClusteredVariantInactiveEntity, ClusteredVariantOperationEntity> {

    public ClusteredVariantInactiveService(
            IHistoryRepository<Long, ClusteredVariantOperationEntity, String> historyRepository,
            Function<ClusteredVariantEntity, ClusteredVariantInactiveEntity> toInactiveEntity,
            Supplier<ClusteredVariantOperationEntity> supplier) {
        super(historyRepository, toInactiveEntity, supplier);
    }
}