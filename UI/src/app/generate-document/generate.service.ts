import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class GenerateService {

  constructor(private http: HttpClient) { }

  url = "http://localhost:8080/"

  createDoc(){
    return this.http.get(this.url+"image/demo");
  }

  downloadDoc(){
    return this.http.get(this.url+"doc/download",  { responseType: 'blob' });
  }
}
