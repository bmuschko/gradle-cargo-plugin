### Version 0.3 (June 26, 2011)

* Exposed local run task introduced in Cargo 1.1.1. The plugin does not favor the [deprecation](http://cargo.codehaus.org/Ant+support)
of the `wait` convention property. It got removed completely. Please use `cargoRunLocal` or `cargoStartLocal` depending on
your use case.
* _Note:_ This release requires you to use Cargo >= 1.1.1.

### Version 0.2 (May 30, 2011)

* Added property for setting `cargo.protocol` to remote container tasks.

### Version 0.1 (May 27, 2011)

* Initial release.