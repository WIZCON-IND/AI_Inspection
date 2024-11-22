import { Component } from '@angular/core';
import {NgFor, NgIf} from '@angular/common';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {UploadServiceService} from './upload-service.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-upload',
  imports: [NgFor, ReactiveFormsModule, NgIf],
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.scss'
})
export class UploadComponent {


  uploadForm: FormGroup;
  imageFiles: { file: File; url: string }[] = [];
  audioFiles: { file: File; url: string }[] = [];

  constructor(private fb: FormBuilder, private http: HttpClient, private uploadService: UploadServiceService,private router: Router) {
    this.uploadForm = this.fb.group({
      images: [null, Validators.required],
      audio: [null, Validators.required],
      documentName: ["", Validators.required],
      address: ["", Validators.required]
    });
  }

  onImageUpload(event: Event): void {
    const files = (event.target as HTMLInputElement).files;
    if (files) {
      const fileArray = Array.from(files); // Convert FileList to Array
      let processedFilesCount = 0; // Counter for processed files

      fileArray.forEach((file) => {
        const reader = new FileReader();
        reader.onload = () => {
          if (reader.result) {
            this.imageFiles.push({ file, url: reader.result as string });
          }
          processedFilesCount++;

          // Check if all files are processed
          if (processedFilesCount === fileArray.length) {
            // Update the form control value after all files are processed
            this.uploadForm.get('images')?.setValue(this.imageFiles.map((item) => item.file));
          }
        };
        reader.readAsDataURL(file);
      });
    }
  }

  onAudioUpload(event: Event): void {
    const files = (event.target as HTMLInputElement).files;
    if (files) {
      const fileArray = Array.from(files); // Convert FileList to Array
      let processedFilesCount = 0; // Counter for processed files

      fileArray.forEach((file) => {
        const reader = new FileReader();
        reader.onload = () => {
          if (reader.result) {
            this.audioFiles.push({ file, url: reader.result as string });
          }
          processedFilesCount++;

          // Check if all files are processed
          if (processedFilesCount === fileArray.length) {
            // Update the form control value after all files are processed
            this.uploadForm.get('audio')?.setValue(this.audioFiles.map((item) => item.file));
          }
        };
        reader.readAsDataURL(file);
      });
    }

    console.log(this.uploadForm.get('audio'))
  }

  isFormValid(): boolean {
    return this.uploadForm.valid && (this.imageFiles.length > 0 || this.audioFiles.length > 0);
  }

  onSubmit(): void {
    if (this.uploadForm.valid) {
      const formData = new FormData();

      // Append image files
      this.imageFiles.forEach((item, index) => {
        formData.append('images', item.file, item.file.name);
      });

      // Append audio files
      this.audioFiles.forEach((item, index) => {
        formData.append('audio', item.file, item.file.name);
      });
      formData.append('documentName',this.uploadForm.controls['documentName'].value )
      formData.append('address',this.uploadForm.controls['address'].value )
      console.log(this.uploadForm.controls['address'].value )

      // Make the HTTP request
      this.uploadService.sendFiles(formData).subscribe((data: any) =>{
        if(data.status){
          alert(data.message)
          this.router.navigate(['/generate']);
        }
      })
    } else {
      alert('Please upload at least one file.');
    }
  }
}
