import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';

/** One entry in the nominee picker — enough to preview who a typed email matches. */
export interface NominatableColleague {
  name: string;
  email: string;
  department: string;
  location: string;
}

/**
 * Who can be nominated — backs the nominee picker on the form.
 *
 * The backend already excludes contractors (see `UserDirectoryController`),
 * so every entry here is always a valid nominee as far as contract type goes.
 */
@Injectable({ providedIn: 'root' })
export class UserDirectoryService {
  private readonly http = inject(HttpClient);

  async listNominatable(): Promise<NominatableColleague[]> {
    return firstValueFrom(
      this.http.get<NominatableColleague[]>(`${environment.apiBaseUrl}/users/nominatable`),
    );
  }
}
