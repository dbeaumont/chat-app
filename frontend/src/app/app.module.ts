
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent } from './app.component';
import { AuthDemoComponent } from './auth-demo/auth-demo.component';
import { AuthInterceptor } from './auth/auth.interceptor';

const routes: Routes = [
  { path: 'auth', component: AuthDemoComponent },
  { path: '**', redirectTo: 'auth' }
];

@NgModule({
  declarations: [AppComponent, AuthDemoComponent],
  imports: [BrowserModule, HttpClientModule, RouterModule.forRoot(routes)],
  providers: [{ provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }],
  bootstrap: [AppComponent]
})
export class AppModule {}
