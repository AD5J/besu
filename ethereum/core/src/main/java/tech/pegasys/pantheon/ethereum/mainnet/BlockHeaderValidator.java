package tech.pegasys.pantheon.ethereum.mainnet;

import tech.pegasys.pantheon.ethereum.ProtocolContext;
import tech.pegasys.pantheon.ethereum.core.BlockHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlockHeaderValidator<C> {

  private static final Logger LOG = LogManager.getLogger();

  private final List<Rule<C>> rules;

  private BlockHeaderValidator(final List<Rule<C>> rules) {
    this.rules = rules;
  }

  public boolean validateHeader(
      final BlockHeader header,
      final BlockHeader parent,
      final ProtocolContext<C> protocolContext,
      final HeaderValidationMode mode) {
    switch (mode) {
      case NONE:
        return true;
      case LIGHT:
        return applyRules(header, parent, protocolContext, Rule::includeInLightValidation);
      case DETACHED_ONLY:
        return applyRules(header, parent, protocolContext, Rule::isDetachedSupported);
      case SKIP_DETACHED:
        return applyRules(header, parent, protocolContext, rule -> !rule.isDetachedSupported());
      case FULL:
        return applyRules(header, parent, protocolContext, rule -> true);
    }
    throw new IllegalArgumentException("Unknown HeaderValidationMode: " + mode);
  }

  public boolean validateHeader(
      final BlockHeader header,
      final ProtocolContext<C> protocolContext,
      final HeaderValidationMode mode) {
    if (mode == HeaderValidationMode.NONE) {
      return true;
    }
    return getParent(header, protocolContext)
        .map(parentHeader -> validateHeader(header, parentHeader, protocolContext, mode))
        .orElse(false);
  }

  private boolean applyRules(
      final BlockHeader header,
      final BlockHeader parent,
      final ProtocolContext<C> protocolContext,
      final Predicate<Rule<C>> filter) {
    return rules
        .stream()
        .filter(filter)
        .allMatch(rule -> rule.validate(header, parent, protocolContext));
  }

  private Optional<BlockHeader> getParent(
      final BlockHeader header, final ProtocolContext<C> context) {
    final Optional<BlockHeader> parent =
        context.getBlockchain().getBlockHeader(header.getParentHash());
    if (!parent.isPresent()) {
      LOG.trace("Invalid block header: cannot determine parent header");
    }
    return parent;
  }

  private static class Rule<C> {
    private final boolean detachedSupported;
    private final AttachedBlockHeaderValidationRule<C> rule;
    private final boolean includeInLightValidation;

    private Rule(
        final boolean detachedSupported,
        final AttachedBlockHeaderValidationRule<C> rule,
        final boolean includeInLightValidation) {
      this.detachedSupported = detachedSupported;
      this.rule = rule;
      this.includeInLightValidation = includeInLightValidation;
    }

    public boolean isDetachedSupported() {
      return detachedSupported;
    }

    public boolean validate(
        final BlockHeader header,
        final BlockHeader parent,
        final ProtocolContext<C> protocolContext) {
      return this.rule.validate(header, parent, protocolContext);
    }

    public boolean includeInLightValidation() {
      return includeInLightValidation;
    }
  }

  public static class Builder<C> {
    private final List<Rule<C>> rules = new ArrayList<>();

    public Builder<C> addRule(final AttachedBlockHeaderValidationRule<C> rule) {
      this.rules.add(new Rule<>(false, rule, true));
      return this;
    }

    public Builder<C> addRule(final DetachedBlockHeaderValidationRule rule) {
      this.rules.add(
          new Rule<>(
              true,
              (header, parent, protocolContext) -> rule.validate(header, parent),
              rule.includeInLightValidation()));
      return this;
    }

    public BlockHeaderValidator<C> build() {
      return new BlockHeaderValidator<>(rules);
    }
  }
}
