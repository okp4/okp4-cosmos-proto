use regex::Regex;
use std::{
    fs, io,
    path::{Path, PathBuf},
};

fn main() {
    let proto_dir: PathBuf = "cosmos-sdk-grpc-client/src/gen/".parse().unwrap();
    apply_patches(&proto_dir);
}

/// Fix clashing type names in prost-generated code. See cosmos/cosmos-rust#154.
fn apply_patches(proto_dir: &Path) {
    for (pattern, replacement) in [
        ("enum Validators", "enum Policy"),
        (
            "stake_authorization::Validators",
            "stake_authorization::Policy",
        ),
    ] {
        patch_file(
            &proto_dir.join("cosmos.staking.v1beta1.rs"),
            &Regex::new(pattern).unwrap(),
            replacement,
        )
        .expect("error patching cosmos.staking.v1beta1.rs");
    }
}

fn patch_file(path: impl AsRef<Path>, pattern: &Regex, replacement: &str) -> io::Result<()> {
    let mut contents = fs::read_to_string(&path)?;
    contents = pattern.replace_all(&contents, replacement).to_string();
    fs::write(path, &contents)
}
