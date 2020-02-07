/*
 * Copyright 2019 EMBL - European Bioinformatics Institute
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
 */
package uk.ac.ebi.eva.accession.deprecate.configuration.batch.listeners;

import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import uk.ac.ebi.eva.accession.core.model.dbsnp.DbsnpClusteredVariantEntity;
import uk.ac.ebi.eva.accession.deprecate.batch.listeners.DeprecationStepProgressListener;

import static uk.ac.ebi.eva.accession.deprecate.configuration.BeanNames.DEPRECATION_PROGRESS_LISTENER;

@Configuration
public class ListenerConfiguration {

    @Bean(DEPRECATION_PROGRESS_LISTENER)
    public StepListenerSupport<DbsnpClusteredVariantEntity, DbsnpClusteredVariantEntity> deprecationProgressListener() {
        return new DeprecationStepProgressListener();
    }
}