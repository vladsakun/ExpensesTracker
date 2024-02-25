package com.emendo.expensestracker

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

abstract class ExpeFeatureExt @Inject constructor(project: Project, objects: ObjectFactory) {

  val namespace: Property<String> = objects.property<String>()

  private val featureHandler = objects.newInstance<FeatureHandler>(project)

  fun features(action: Action<FeatureHandler>) {
    action.execute(featureHandler)
  }

  abstract class FeatureHandler @Inject constructor(private val project: Project) {
    // Todo add expe features
  }
}

internal fun Project.setupExpeFeatures() {
  extensions.create("expe", ExpeFeatureExt::class.java, this)
}