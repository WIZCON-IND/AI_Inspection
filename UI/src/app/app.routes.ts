import { Routes } from '@angular/router';
import {LoginComponent} from './login/login.component';
import {UploadComponent} from './upload/upload.component';
import {GenerateDocumentComponent} from './generate-document/generate-document.component';

export const routes: Routes = [
  {path: "", component: LoginComponent},
  {path: "upload", component: UploadComponent},
  {path: "generate", component: GenerateDocumentComponent}
];
