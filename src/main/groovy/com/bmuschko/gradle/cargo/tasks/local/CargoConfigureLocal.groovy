package com.bmuschko.gradle.cargo.tasks.local

class CargoConfigureLocal extends LocalCargoContainerTask {
	CargoConfigureLocal() {
		action = 'configure'
		description = 'Configures the container'
	}
}
