import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { DEMO_USERS } from '../../core/models/user.model';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly formBuilder = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  protected readonly demoUsers = DEMO_USERS;
  protected readonly submitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly form = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  /** True once the control should start showing its error. */
  protected showError(controlName: 'email' | 'password'): boolean {
    const control = this.form.controls[controlName];
    return control.invalid && (control.touched || control.dirty);
  }

  protected useDemoAccount(email: string): void {
    this.form.patchValue({ email, password: 'demo' });
    this.errorMessage.set(null);
  }

  protected async onSubmit(): Promise<void> {
    this.errorMessage.set(null);

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const { email, password } = this.form.getRawValue();
    const failure = await this.auth.signIn(email, password);
    this.submitting.set(false);

    if (failure) {
      this.errorMessage.set(failure);
      return;
    }

    // Coordinators land on the review dashboard, everyone else on the form.
    const home = this.auth.user()?.role === 'coordinator' ? '/review' : '/nominate';
    const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') ?? home;
    void this.router.navigateByUrl(returnUrl);
  }
}
