import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class UploadServiceService {

  constructor(private http: HttpClient) { }

  url = "http://localhost:8080/file/upload"

  sendFiles(formData: any){
    console.log(...formData);


    return this.http.post(this.url, formData);

  }
}
