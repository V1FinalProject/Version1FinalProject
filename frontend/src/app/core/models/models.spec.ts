import { describe, expect, it } from 'vitest';

import {
  NOMINATION_CATEGORIES,
  CORE_VALUES,
  CURRENT_QUARTER,
  NARRATIVE_MIN_LENGTH,
  NARRATIVE_MAX_LENGTH,
} from './nomination.model';

describe('NOMINATION_CATEGORIES', () => {
  // These ids are the contract with the backend NominationCategory enum.
  const expectedIds = [
    'collaboration-and-engagement',
    'customer-impact',
    'innovation-and-growth',
    'performance-and-efficiency',
    'quality-and-compliance',
  ];

  it('exposes the five categories the backend recognises', () => {
    expect(NOMINATION_CATEGORIES.map((c) => c.id)).toEqual(expectedIds);
  });

  it('gives every category a label and examples', () => {
    for (const category of NOMINATION_CATEGORIES) {
      expect(category.label.length).toBeGreaterThan(0);
      expect(category.examples.length).toBeGreaterThan(0);
    }
  });
});

describe('nomination constants', () => {
  it('defines six core values', () => {
    expect(CORE_VALUES).toHaveLength(6);
    expect(CORE_VALUES).toContain('Excellence');
  });

  it('keeps the narrative length window sane', () => {
    expect(NARRATIVE_MIN_LENGTH).toBeGreaterThan(0);
    expect(NARRATIVE_MIN_LENGTH).toBeLessThan(NARRATIVE_MAX_LENGTH);
  });

  it('names the current quarter', () => {
    expect(CURRENT_QUARTER).toMatch(/^Q[1-4]\s\d{4}$/);
  });
});
