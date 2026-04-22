from __future__ import annotations

import json
import re
from dataclasses import dataclass
from pathlib import Path


ROOT = Path(r"D:\ideaJavaContent\demo1\frontend\src\views")
REPORT = Path(r"D:\ideaJavaContent\demo1\frontend\src\views\_encoding_fix_report.json")


BROKEN_TAG_REPLACEMENTS = {
    "??/span>": "</span>",
    "??/h2>": "</h2>",
    "??/h3>": "</h3>",
    "??/h4>": "</h4>",
    "??/p>": "</p>",
    "??/a>": "</a>",
    "??/li>": "</li>",
    "??/div>": "</div>",
    "??/el-button>": "</el-button>",
}


@dataclass
class FileResult:
    file: str
    replaced_map_items: int
    replaced_map_occurrences: int
    fixed_broken_tags: int
    fffd_count: int
    question_count: int


def build_mojibake_map() -> dict[str, str]:
    """
    Build a safe whitelist map from known mojibake samples.
    """
    sample_path = ROOT / "_recovered_strings.json"
    if not sample_path.exists():
        return {}

    items = json.loads(sample_path.read_text(encoding="utf-8"))
    out: dict[str, str] = {}
    for item in items:
        if not isinstance(item, str) or not item:
            continue
        try:
            candidate = item.encode("utf-8").decode("gb18030")
        except UnicodeDecodeError:
            continue
        if candidate == item:
            continue
        if "\ufffd" in candidate:
            continue
        # Keep only CJK-heavy outputs; avoid accidental ASCII transformations.
        cjk = sum(1 for ch in candidate if "\u4e00" <= ch <= "\u9fff")
        if cjk == 0:
            continue
        out[item] = candidate
    return out


def apply_whitelist_replacements(text: str, mp: dict[str, str]) -> tuple[str, int, int]:
    changed_items = 0
    changed_count = 0
    for bad in sorted(mp.keys(), key=len, reverse=True):
        good = mp[bad]
        cnt = text.count(bad)
        if cnt:
            text = text.replace(bad, good)
            changed_items += 1
            changed_count += cnt
    return text, changed_items, changed_count


def apply_broken_tag_fixes(text: str) -> tuple[str, int]:
    total = 0
    for bad, good in BROKEN_TAG_REPLACEMENTS.items():
        cnt = text.count(bad)
        if cnt:
            text = text.replace(bad, good)
            total += cnt
    return text, total


def main() -> None:
    mp = build_mojibake_map()
    results: list[FileResult] = []

    for fp in sorted(ROOT.glob("*.vue")):
        original = fp.read_text(encoding="utf-8", errors="replace")
        updated = original

        updated, map_items, map_occ = apply_whitelist_replacements(updated, mp)
        updated, fixed_tags = apply_broken_tag_fixes(updated)

        if updated != original:
            fp.write_text(updated, encoding="utf-8", newline="\n")

        results.append(
            FileResult(
                file=fp.name,
                replaced_map_items=map_items,
                replaced_map_occurrences=map_occ,
                fixed_broken_tags=fixed_tags,
                fffd_count=updated.count("\ufffd"),
                question_count=updated.count("?"),
            )
        )

    report = {
        "mapping_size": len(mp),
        "results": [r.__dict__ for r in results],
        "high_risk_files": [r.file for r in results if r.fffd_count > 0],
    }
    REPORT.write_text(json.dumps(report, ensure_ascii=False, indent=2), encoding="utf-8")
    print(f"done. mapping={len(mp)} report={REPORT}")


if __name__ == "__main__":
    main()

