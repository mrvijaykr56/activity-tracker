import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './component/home/home.component';
import { LoginComponent } from './component/login/login.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { HeaderComponent } from './component/header/header.component';
import { FooterComponent } from './component/footer/footer.component';
import { RegisterComponent } from './component/register/register.component';
import { LoadingComponent } from './shared/loading.component';
import { NotificationComponent } from './shared/notification.component';
import { ConfirmComponent } from './shared/confirm.component';
import { LoadingInterceptor } from './interceptor/loading.interceptor';
import { ErrorInterceptor } from './interceptor/error.interceptor';
import { AuthInterceptor } from './interceptor/auth.interceptor';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    LoginComponent,
    HeaderComponent,
    FooterComponent,
    RegisterComponent,
    LoadingComponent,
    NotificationComponent,
    ConfirmComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutingModule,
    HttpClientModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

