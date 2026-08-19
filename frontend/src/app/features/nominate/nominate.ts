import { Component, ElementRef, computed, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  AbstractControl,
  FormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import {
  ALLOWED_EMAIL_DOMAIN,
  CORE_VALUES,
  CURRENT_QUARTER,
  NARRATIVE_MAX_LENGTH,
  NARRATIVE_MIN_LENGTH,
  NOMINATION_CATEGORIES,
  NominationReceipt,
  NominationSubmission,
  PROGRAMME_URL,
} from '../../core/models/nomination.model';
import { AuthService } from '../../core/services/auth.service';
import { NominationService } from '../../core/services/nomination.service';

/** Control names in display order — used to focus the first error on submit. */
const FIELD_ORDER = ['nomineeName', 'nomineeEmail', 'what', 'how', 'categoryId'] as const;

type FieldName = (typeof FIELD_ORDER)[number];

@Component({
  selector: 'app-nominate',
  imports: [ReactiveFormsModule],
  templateUrl: './nominate.html',
  styleUrl: './nominate.scss',
})
export class Nominate {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly nominations = inject(NominationService);
  private readonly host: ElementRef<HTMLElement> = inject(ElementRef);

  protected readonly categories = NOMINATION_CATEGORIES;
  protected readonly coreValues = CORE_VALUES;
  protected readonly quarter = CURRENT_QUARTER;
  protected readonly programmeUrl = PROGRAMME_URL;
  protected readonly maxLength = NARRATIVE_MAX_LENGTH;
  protected readonly minLength = NARRATIVE_MIN_LENGTH;
  protected readonly emailDomain = ALLOWED_EMAIL_DOMAIN;

  /** The signed-in nominator — stamped onto the submission automatically. */
  protected readonly nominator = this.auth.user;
  protected readonly firstName = computed(
    () => this.nominator()?.name.split(' ')[0] ?? 'there',
  );

  protected readonly submitting = signal(false);
  protected readonly submitError = signal<string | null>(null);
  protected readonly receipt = signal<NominationReceipt | null>(null);
  /** Kept so the confirmation screen can name the nominee after the form resets. */
  protected readonly confirmedNominee = signal('');

  /**
   * The case study requires self-nomination to be blocked.
   *
   * Declared as an arrow property (not a method) so `this` stays bound when
   * Angular calls it standalone — and declared *before* `form`, because class
   * fields initialise in source order.
   */
  private readonly notSelf = (control: AbstractControl): ValidationErrors | null => {
    const entered = String(control.value ?? '')
      .trim()
      .toLowerCase();
    const mine = this.auth.user()?.email.toLowerCase();
    return entered && mine && entered === mine ? { selfNomination: true } : null;
  };

  /** Nominees must be colleagues — reject anything outside the Version 1 domain. */
  private readonly companyEmail = (control: AbstractControl): ValidationErrors | null => {
    const entered = String(control.value ?? '')
      .trim()
      .toLowerCase();
    if (!entered || !entered.includes('@')) {
      return null; // Validators.email already reports this.
    }
    return entered.endsWith(`@${ALLOWED_EMAIL_DOMAIN}`) ? null : { wrongDomain: true };
  };

  private readonly narrativeValidators = [
    Validators.required,
    Validators.minLength(NARRATIVE_MIN_LENGTH),
    Validators.maxLength(NARRATIVE_MAX_LENGTH),
  ];

  protected readonly form = this.formBuilder.nonNullable.group({
    nomineeName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(120)]],
    nomineeEmail: [
      '',
      [Validators.required, Validators.email, this.companyEmail, this.notSelf],
    ],
    what: ['', this.narrativeValidators],
    how: ['', this.narrativeValidators],
    categoryId: ['', [Validators.required]],
    emailReceipt: [false],
  });

  private readonly whatValue = toSignal(this.form.controls.what.valueChanges, {
    initialValue: '',
  });
  private readonly howValue = toSignal(this.form.controls.how.valueChanges, {
    initialValue: '',
  });
  protected readonly whatLength = computed(() => this.whatValue().length);
  protected readonly howLength = computed(() => this.howValue().length);

  protected readonly categoryValue = toSignal(this.form.controls.categoryId.valueChanges, {
    initialValue: '',
  });
  protected readonly selectedCategory = computed(
    () => this.categories.find((category) => category.id === this.categoryValue()) ?? null,
  );

  /** '', 'near' or 'over' — drives the colour of the character counter. */
  protected counterState(length: number): '' | 'near' | 'over' {
    if (length > NARRATIVE_MAX_LENGTH) {
      return 'over';
    }
    return length > NARRATIVE_MAX_LENGTH * 0.9 ? 'near' : '';
  }

  protected showError(controlName: FieldName): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || control.dirty);
  }

  protected async onSubmit(): Promise<void> {
    this.submitError.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.focusFirstInvalid();
      return;
    }

    const nominator = this.nominator();
    if (!nominator) {
      this.submitError.set('Your session has expired. Please sign in again.');
      return;
    }

    const value = this.form.getRawValue();
    const submission: NominationSubmission = {
      nomineeName: value.nomineeName.trim(),
      nomineeEmail: value.nomineeEmail.trim().toLowerCase(),
      what: value.what.trim(),
      how: value.how.trim(),
      categoryId: value.categoryId,
      emailReceipt: value.emailReceipt,

      nominatorId: nominator.id,
      nominatorName: nominator.name,
      nominatorEmail: nominator.email,
      practice: nominator.practice,
      location: nominator.location,

      quarter: CURRENT_QUARTER,
      submittedAt: new Date().toISOString(),
    };

    this.submitting.set(true);
    try {
      const receipt = await this.nominations.submit(submission);
      this.confirmedNominee.set(submission.nomineeName);
      this.receipt.set(receipt);
      this.form.reset();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } catch {
      this.submitError.set(
        'We couldn’t submit your nomination. Please check your connection and try again.',
      );
    } finally {
      this.submitting.set(false);
    }
  }

  private focusFirstInvalid(): void {
    const name = FIELD_ORDER.find((field) => this.form.controls[field].invalid);
    if (name) {
      this.host.nativeElement.querySelector<HTMLElement>(`#${name}`)?.focus();
    }
  }
}
