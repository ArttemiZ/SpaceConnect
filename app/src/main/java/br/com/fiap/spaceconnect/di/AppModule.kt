package br.com.fiap.spaceconnect.di

import android.content.Context
import androidx.room.Room
import br.com.fiap.spaceconnect.BuildConfig
import br.com.fiap.spaceconnect.data.model.FavoriteDao
import br.com.fiap.spaceconnect.data.model.SpaceConnectDatabase
import br.com.fiap.spaceconnect.data.remote.api.NasaApiService
import br.com.fiap.spaceconnect.data.repository.FavoriteRepositoryImpl
import br.com.fiap.spaceconnect.data.repository.NasaRepositoryImpl
import br.com.fiap.spaceconnect.domain.repository.FavoriteRepository
import br.com.fiap.spaceconnect.domain.repository.NasaRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.NASA_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideNasaApiService(retrofit: Retrofit): NasaApiService =
        retrofit.create(NasaApiService::class.java)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SpaceConnectDatabase =
        Room.databaseBuilder(context, SpaceConnectDatabase::class.java, "space_connect_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideFavoriteDao(db: SpaceConnectDatabase): FavoriteDao = db.favoriteDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNasaRepository(impl: NasaRepositoryImpl): NasaRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(impl: FavoriteRepositoryImpl): FavoriteRepository
}